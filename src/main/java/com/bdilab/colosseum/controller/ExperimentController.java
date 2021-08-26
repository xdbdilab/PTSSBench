package com.bdilab.colosseum.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.benchmark.execute.MultiAgExecutor;
import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.domain.ExperimentRun;
import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.domain.sse.ProcessSseEmitters;
import com.bdilab.colosseum.enums.ExperimentRunningStatus;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.global.Context;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.ExperimentService;
import com.bdilab.colosseum.service.PipelineService;
import com.bdilab.colosseum.service.SystemEnvService;
import com.bdilab.colosseum.service.WorkloadService;
import com.bdilab.colosseum.vo.ExperimentDetailVO;
import com.bdilab.colosseum.vo.SystemEnvDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * @ClassName ExperimentController 实验管理API
 * @Author wx
 * @Date 2020/12/31 0031 16:40
 **/
@Api(tags = "实验管理API")
@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    SystemEnvService systemEnvService;

    @Autowired
    WorkloadService workloadService;

    @Autowired
    PipelineService pipelineService;


    @Value("${result.path}")
    private String resultPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    int i=1;

    @ApiOperation("算法中调用硬件环境进行调优实验的时候调用")
    @GetMapping("/getPerformance")
    public String getPerformance(@ApiParam(name = "conversationId", value = "算法与后端会话ID", required = true, type = "String")
                                 @RequestParam("conversationId") String conversationId,
                                 @ApiParam(name = "paramList", value = "待调优参数集合,格式为参数名=参数值", required = true, type = "String")
                                 @RequestParam("paramList") String paramList,
                                 @ApiParam(name = "performance", value = "性能指标", required = false, type = "String")
                                 @RequestParam(value = "performance",required = false) String performance) {
        Workload workload = Context.getInstance().getWorkload(conversationId);
        // 检测环境是否存在
        if (!systemEnvService.checkBySystemEnvId((Long) workload.getParam("envId"))) {
            // 环境不存在
            return "实验环境不存在";
        }
        //String result = experimentService.getPerformance(workload, paramList, performance);
        String result=Integer.toString((int)(Math.random()*100000));
        logger.info("/getPerformance\nperformance_{}:{}",performance,result);
        if(result == null){
            return Constant.PERFORMANCE_ERROR;
        }
        //--- TODO 将result保存到文件中 ---
        MultiAgExecutor executor = Context.getInstance().getExecutor(conversationId);
        //得到当前实验正在进行的算法
        MultiAgExecutor.ExecutorAg curExecutorAg = executor.getCurExecutorAg();
        //得到当前实验正在进行算法的Kubeflow RunId
        //String curRunId = executor.getCurRunId();

        //将每一次调优结果存入结果文件
        String resultFilePath=workload.getParam("resultFilePath").toString();
        File resultFile = new File(resultFilePath);
        JSONObject jsonObject=null;
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
            InputStream is = new FileInputStream(resultFile);
            jsonObject=JSONObject.parseObject(IOUtils.toString(is, "utf-8"));
            JSONObject algorithmJson=new JSONObject();
            JSONObject paramListJson=new JSONObject();
            String[] strs=paramList.split("[\\=\\,]");
            for(int i=0;i<strs.length;i+=2){
                paramListJson.put(strs[i],strs[i+1]);
            }
            JSONObject resultByRunIdJson=new JSONObject();
            resultByRunIdJson.put("paramList",paramListJson);
            resultByRunIdJson.put(performance,result);
            if(!jsonObject.containsKey(curExecutorAg.getId().toString())){
                JSONObject js=new JSONObject();
                js.put(executor.getCurRunId()+"_"+1,resultByRunIdJson);
                algorithmJson.put("algorithmName",curExecutorAg.getAlgorithmName());
                algorithmJson.put("result",js);
                jsonObject.put(curExecutorAg.getId().toString(),algorithmJson);
                i=1;
            }else{
                JSONObject r=jsonObject.getJSONObject(curExecutorAg.getId().toString()).getJSONObject("result");
                r.put(executor.getCurRunId()+"_"+(++i),resultByRunIdJson);
                jsonObject.getJSONObject(curExecutorAg.getId().toString()).put("result",r);
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
            bw.write(jsonObject.toString());
            bw.close();
            is.close();
            br.close();
        }catch (Exception e) {
            logger.error("记录调优结果出错",e);
        }

        //TODO 通过sse推送到前端
//        if(ProcessSseEmitters.getSseEmitterByKey(conversationId)==null){
//            SseEmitter emitter = new SseEmitter(0L);
//            try {
//                ProcessSseEmitters.addSseEmitters(conversationId,emitter);
//                ProcessSseEmitters.getSseEmitterByKey(conversationId).send(result);
//            }catch (Exception e){
//                emitter.completeWithError(e);
//            }
//        }else{
//            try{
//                ProcessSseEmitters.getSseEmitterByKey(conversationId).send(result);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    @ApiOperation("添加实验的时候调用")
    @PostMapping("/add")
    public ResponseResult add(@ApiParam(name = "experimentName", value = "实验名称", required = true, type = "String")
                              @RequestParam("experimentName") String experimentName,
                              @ApiParam(name = "description", value = "实验描述", required = false, type = "String")
                              @RequestParam(value = "description", required = false) String description,
                              @ApiParam(name = "sysEnvId", value = "环境配置ID", required = true, type = "Long")
                              @RequestParam(value = "sysEnvId") Long sysEnvId,
                              @ApiParam(name = "allParams", value = "参数列表，格式[json{参数},json{参数},...]}", required = false, type = "String")
                              @RequestParam(value = "allParams",required = false) String allParams,
                              @ApiParam(name = "blackList", value = "参数黑名单，格式[参数名，参数名，...]", required =false, type = "String")
                              @RequestParam(value = "blackList",required = false) String blackList,
                              @ApiParam(name = "whiteList", value = "参数白名单，格式[参数名，参数名，...]", required = false, type = "String")
                              @RequestParam(value = "whiteList",required = false) String whiteList,
                              // 修改标记+++++++++++++++++++++++
                              @ApiParam(name = "algorithmIds", value = "多个算法ID，以逗号分隔：id1,id2", required = true, type = "List<Long>")
                              @RequestParam("algorithmIds") List<Long> algorithmIds,
                              @ApiParam(name = "performance", value = "性能指标", required = true, type = "String")
                              @RequestParam("performance") String performance,
                              @ApiParam(name = "metricsSetting", value = "度量指标,如果不填的话默认传递选择的性能指标返回", required = false, type = "String")
                              @RequestParam(value = "metricsSetting", required = false) String metricsSetting,
                              @ApiParam(name = "resultSetting", value = "结果呈现方式", required = true, type = "String")
                              @RequestParam("resultSetting") String resultSetting,
                              @ApiParam(value = "实验logo",required = false)
                                  @RequestParam(required = false) MultipartFile logo,
                              HttpSession session) throws Exception{
        // 获取userId
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        JSONArray jsonArray=JSONArray.parseArray(allParams);
        JSONArray jsonArray1=new JSONArray();
        if(!experimentService.checkAlgorithms(algorithmIds, userId)){
                return new ResponseResult(HttpCode.BAD_REQUEST.getCode(),"chosen algorithms invalid", false);
        }
        if(whiteList==null||whiteList.equals("")||whiteList.equals("[]")){
            whiteList="";
            jsonArray1=JSONArray.parseArray(allParams);
        }else{
            String[] strs=whiteList.replaceAll("(\"|\\[|])","")
                    .split(",");
            for(int i=0;i<jsonArray.size();i++){
                for(String str:strs){
                    if((jsonArray.getJSONObject(i).get("name")).equals(str)){
                        jsonArray1.add(jsonArray.getJSONObject(i));
                    }
                }
            }
            if(!(blackList==null||blackList.equals("")||blackList.equals("[]"))){
                String[] strs1=blackList.replaceAll("(\"|\\[|])","")
                        .split(",");
                for(int i=0;i<jsonArray.size();i++){
                    for(String str:strs1){
                        if((jsonArray.getJSONObject(i).get("name")).equals(str)){
                            jsonArray1.add(jsonArray.getJSONObject(i));
                        }
                    }
                }
            }
        }
        if(blackList==null||blackList.equals("")||blackList.equals("[]")){
            blackList="";
        }
        int insert = experimentService.add(experimentName,description,userId,sysEnvId,jsonArray1.toString(),blackList,whiteList,JSONObject.toJSONString(algorithmIds),performance,metricsSetting,resultSetting,logo);
        if (insert > 0) {
            return new ResponseResult(HttpCode.OK.getCode(),"Add experiment succeeded", true);
        }
        return new ResponseResult(HttpCode.SERVER_ERROR.getCode(),"Failed to add experiment", false);
    }

    @ApiOperation("修改实验的时候调用")
    @PutMapping("/update/{experimentId}")
    public ResponseResult update(@PathVariable("experimentId") Long experimentId,
                                 @ApiParam(name = "description", value = "实验描述", required = false, type = "String")
                                 @RequestParam(value = "description", required = false) String description,
                                 @ApiParam(name = "sysEnvId", value = "环境配置ID", required = false, type = "Long")
                                 @RequestParam(value = "sysEnvId", required = false) Long sysEnvId,
                                 @ApiParam(name = "allParams", value = "参数列表，格式[json{参数},json{参数},...]", required = false, type = "String")
                                 @RequestParam(value = "allParams", required = false) String allParams,
                                 @ApiParam(name = "blackList", value = "参数黑名单，格式[参数名，参数名，...]", required = false, type = "String")
                                 @RequestParam(value = "blackList", required = false) String blackList,
                                 @ApiParam(name = "whiteList", value = "参数白名单，格式[参数名，参数名，...]", required = false, type = "String")
                                 @RequestParam(value = "whiteList", required = false) String whiteList,
                                 // 修改标记+++++++++++++++++++++++
                                 @ApiParam(name = "algorithmIds", value = "多个算法ID的数组，格式[算法id,算法id,...]", required = false, type = "List<Long>")
                                 @RequestParam(value = "algorithmIds", required = false) List<Long> algorithmIds,
                                 @ApiParam(name = "performance", value = "性能指标", required = false, type = "String")
                                 @RequestParam(value = "performance", required = false) String performance,
                                 @ApiParam(name = "metricsSetting", value = "度量指标", required = false, type = "String")
                                 @RequestParam(value = "metricsSetting", required = false) String metricsSetting,
                                 @ApiParam(name = "resultSetting", value = "结果呈现方式", required = false, type = "String")
                                 @RequestParam(value = "resultSetting", required = false) String resultSetting,
                                 @ApiParam(value = "实验logo",required = false)
                                     @RequestParam(required = false) MultipartFile logo,
                                 HttpSession session) throws Exception{
        // 验证experiment是否存在
        if (!experimentService.checkByExperimentId(experimentId)) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "The experiment does not exist", false);
        }
        // 获取userId
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        if(algorithmIds != null){
            if(!experimentService.checkAlgorithms(algorithmIds,userId)){
                return new ResponseResult(HttpCode.BAD_REQUEST.getCode(),"chosen algorithms invalid", false);
            }
        }
        JSONArray jsonArray=JSONArray.parseArray(allParams);
        JSONArray jsonArray1=new JSONArray();
        if(whiteList==null||whiteList.equals("")||whiteList.equals("[]")){
            whiteList="";
            jsonArray1=JSONArray.parseArray(allParams);
        }else{
            String[] strs=whiteList.replaceAll("(\"|\\[|])","")
                    .split(",");
            for(int i=0;i<jsonArray.size();i++){
                for(String str:strs){
                    if((jsonArray.getJSONObject(i).get("name")).equals(str)){
                        jsonArray1.add(jsonArray.getJSONObject(i));
                    }
                }
            }
            if(!(blackList==null||blackList.equals("")||blackList.equals("[]"))){
                String[] strs1=blackList.replaceAll("(\"|\\[|])","")
                        .split(",");
                for(int i=0;i<jsonArray.size();i++){
                    for(String str:strs1){
                        if((jsonArray.getJSONObject(i).get("name")).equals(str)){
                            jsonArray1.add(jsonArray.getJSONObject(i));
                        }
                    }
                }
            }
        }
        if(blackList==null||blackList.equals("")||blackList.equals("[]")){
            blackList="";
        }
        int update = experimentService.update(experimentId,description,sysEnvId,jsonArray1.toString(),blackList,whiteList,JSONObject.toJSONString(algorithmIds),performance,metricsSetting,resultSetting,logo);
        if (update > 0) {
            return new ResponseResult(HttpCode.OK.getCode(),"Update experiment succeeded", true);
        }
        return new ResponseResult(HttpCode.SERVER_ERROR.getCode(),"Update experiment failed", false);
    }

    @ApiOperation("查看实验列表的时候调用")
    @GetMapping("/getList")
    public ResponseResult getList(HttpSession session) {
        // 获取userId
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        return new ResponseResult(HttpCode.OK, experimentService.getList(userId));
    }

    @ApiOperation("点击某个实验查看详情时调用")
    @GetMapping("/getDetail/{experimentId}")
    public ResponseResult getDetail(@PathVariable("experimentId") Long experimentId) {
        // 验证experiment是否存在
        if (!experimentService.checkByExperimentId(experimentId)) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "The experiment does not exist", false);
        }
        return new ResponseResult(HttpCode.OK, experimentService.getByExperimentId(experimentId));
    }

    @ApiOperation("点击运行实验时调用")
    @GetMapping("/run/{experimentId}")
    public ResponseResult run(@PathVariable("experimentId") Long experimentId,
                              @ApiParam(name = "execParamsInJson", value = "工作负载执行时所需参数集合的json串", required = true, type = "String")
                              @RequestParam(value = "execParamsInJson") String execParamsInJson,
                              @ApiParam(name = "restrictNumber", value = "算法执行的评价次数", required = true, type = "Integer")
                              @RequestParam(value = "restrictNumber") Integer restrictNumber) {
        Map<String,String> execParams = JSONObject.parseObject(execParamsInJson, Map.class);
        // TODO 这里要对生成的execParams，根据数据库中的参数一一比对
        // 验证experiment是否存在
        if (!experimentService.checkByExperimentId(experimentId)) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "实验不存在！", false);
        }
        // 此处需要考虑异步实验，先返回实验运行开始的结果，等到实验结束后提示用户实验结束并引导至实验结果查看页面查看实验结果
        // 02.21更新:实际上用户点击运行实验后，只是将参数传递到kubeflow，
        // 指定运行pipeline并返回一个run_id存储到experiment_run表中，实际上实验并没有运行结束。
        // 需要开发新的java接口由算法端调用传递组件运行结果给工程侧
        // 所以此处有一个判断问题，同一个实验环境同时只能运行一个实验，
        // 那么用户在点击运行的时候需要先判断experiment_run表中是否有相同实验id且status处于运行状态的，则提示不可运行
        if (experimentService.checkRunningExperimentByExperimentId(experimentId)) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "Sorry, the experiment is running now, please wait for the end of the experiment, and then try again, thank you!", false);
        }
        Map<String, Object> resultMap = experimentService.run(experimentId,execParams,restrictNumber);
        if (!resultMap.get("isSucceed").equals(true)) {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "实验运行失败", false);
        }
        // 运行实验成功返回sse会话ID，方便后续报告组件运行状态
        return new ResponseResult(HttpCode.OK.getCode(), "实验开始运行",  true, resultMap.get("conversationId"));
    }

    @ApiOperation("点击查看历史实验结果时调用")
    @GetMapping("/getResultList")
    public ResponseResult getResultList(@ApiParam(name = "experimentId", value = "实验ID", type = "Long")
                                        @RequestParam("experimentId") Long experimentId,
                                        HttpSession session) {
        // 获取userId
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        return new ResponseResult(HttpCode.OK, experimentService.getResultList(experimentId));
    }

    @ApiOperation("点击查看某个实验结果时调用")
    @GetMapping("/getResultDetail/{resultId}")
    @Deprecated
    public ResponseResult getResultDetail(@PathVariable("resultId") Long resultId) {
        // 验证experiment是否存在
        if (!experimentService.checkByResultId(resultId)) {
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "实验结果不存在！", false);
        }
        return new ResponseResult(HttpCode.OK, experimentService.getResultDetail(resultId));
    }

//    @ApiOperation("终止实验时调用")
//    @GetMapping("/terminate/{resultId}")
//    public ResponseResult terminate(@PathVariable("resultId") Long resultId) {
//        // 验证experiment是否存在
//        if (!experimentService.checkByResultId(resultId)) {
//            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "实验结果不存在！", false);
//        }
//        if (!experimentService.terminate(resultId)) {
//            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "终止实验失败", false);
//        }
//        return new ResponseResult(HttpCode.OK.getCode(), "终止实验成功", true);
//    }

    @ApiOperation("删除实验时调用")
    @DeleteMapping("/delete/{experimentId}")
    public ResponseResult delete(@PathVariable("experimentId") Long experimentId) {
        // 验证experiment是否存在
        if (!experimentService.checkByExperimentId(experimentId)) {
            logger.info("========================");
            logger.info("实验不存在！！！！");
            return new ResponseResult(HttpCode.BAD_REQUEST.getCode(), "Experiment does not exist！", false);
        }
        if (!experimentService.delete(experimentId)) {
            logger.info("========================");
            logger.info("{} : 实验删除失败", experimentId);
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "Failed to delete experiment", false);
        }
        logger.info("========================");
        logger.info("{} : 实验删除成功", experimentId);
        return new ResponseResult(HttpCode.OK.getCode(), "Delete experiment succeeded", true);
    }

    @Deprecated
    @ApiOperation("python端调用该接口，报告执行结果及结果地址文件")
    @PostMapping("/executeTask")
    public ResponseResult executeTask(@ApiParam(name = "experimentRunId", value = "实验运行ID", required = true, type = "Long")
                                      @RequestParam("experimentRunId") Long experimentRunId,
                                      @ApiParam(name = "conversationId", value = "会话ID", required = true, type = "String")
                                      @RequestParam("conversationId") String conversationId,
                                      @ApiParam(name = "componentName", value = "组件名", required = true, type = "String")
                                      @RequestParam("componentName") String componentName,
                                      @ApiParam(name = "resultFilePath", value = "结果文件路径", type = "String")
                                      @RequestParam(value = "resultFilePath", required = false) String resultFilePath) {
        if (experimentService.pushData(experimentRunId, conversationId, componentName, resultFilePath)) {
            return new ResponseResult(HttpCode.OK.getCode(), "已完成任务", true);
        }
        return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "执行任务失败，当前流程已被暂停或中止", false);
    }

    // TODO 这里要改了，如果还需要获取runId，要从执行器.getCurRunId获取
    @ApiOperation("python端调用该接口，报告调优最优值")
    @PostMapping("/report/data")
    public ResponseResult reportData(@RequestParam("conversationId") String conversationId,
                                     @RequestParam("data") String data) {
        Workload workload = Context.getInstance().getWorkload(conversationId);
        MultiAgExecutor executor = Context.getInstance().getExecutor(conversationId);
        //得到当前实验正在进行的算法
        MultiAgExecutor.ExecutorAg curExecutorAg = executor.getCurExecutorAg();
        //得到当前实验正在进行算法的Kubeflow RunId
        String curRunId = executor.getCurRunId();

        //将最优解存入结果文件
        String resultFilePath=workload.getParam("resultFilePath").toString();
        File resultFile = new File(resultFilePath);
        JSONObject jsonObject=null;
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
            InputStream is = new FileInputStream(resultFile);
            jsonObject=JSONObject.parseObject(IOUtils.toString(is, "utf-8"));

            //对data进行处理
            JSONObject js=JSONObject.parseObject(data);
            JSONObject jsonObject1=new JSONObject();
            for(String key:js.keySet()){
                if(key.equals("paramList")){
                    JSONObject js1=js.getJSONObject(key);
                    jsonObject1.put("paraList",js1);
                }else{
                    jsonObject1.put(key,js.get(key));
                }
            }
            JSONObject jsonObject2=jsonObject.getJSONObject(curExecutorAg.getId().toString());
            jsonObject2.put("data",jsonObject1);
            jsonObject.put(curExecutorAg.getId().toString(),jsonObject2);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
            bw.write(jsonObject.toString());
            bw.close();
            is.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //继续执行下一个算法
        if(executor.hasNextAg()){
            String runId = executor.execute(pipelineService);
            if(runId == null){
                logger.error("executor.execute() failed!");
                //TODO 清理善后工作 并 sse告知前端
                Context.getInstance().removeWorkload(conversationId);
                Context.getInstance().removeExecutor(conversationId);
            }else {
                experimentService.updateRunIdINExperimentRun((Long)workload.getParam("experimentRunId"),runId);
            }
        }else {
            experimentService.updateStatusINExperimentRun((Long)workload.getParam("experimentRunId"), ExperimentRunningStatus.RUNNINGSUCCESS,true);
            //TODO 所有算法执行完毕 sse汇报，做一些清理工作（清理SSE、WorkLoad、执行器）
            Context.getInstance().removeWorkload(conversationId);
            Context.getInstance().removeExecutor(conversationId);
        }

        return new ResponseResult(HttpCode.OK.getCode(), "收到", true);
    }

    //TODO 这里报告错误后，要执行一些清理善后工作
    @ApiOperation("python端调用该接口，报告错误")
    @PostMapping("/report/error")
    public ResponseResult reportError(@RequestParam("conversationId") String conversationId,
                                     @RequestParam("error_body") String error_body) {
        logger.info("/report/error\nconversationId:{}\nerror_body:{}",conversationId,error_body);
        Workload workload = Context.getInstance().getWorkload(conversationId);
        MultiAgExecutor executor = Context.getInstance().getExecutor(conversationId);
        //得到当前实验正在进行的算法
        MultiAgExecutor.ExecutorAg curExecutorAg = executor.getCurExecutorAg();
        experimentService.updateStatusINExperimentRun(curExecutorAg.getId(),ExperimentRunningStatus.RUNNINGFAIL,true);
        return new ResponseResult(HttpCode.OK.getCode(), "收到错误报告", true);
    }

    @ApiOperation("运行实验前调用,获取执行参数")
    @GetMapping("/{experimentId}/getExecParams")
    public  ResponseResult getExecParams(@PathVariable("experimentId") Long experimentId,
                                         HttpSession session) throws Exception{
        Long userId = Long.valueOf(session.getAttribute("user_id").toString());
        ExperimentDetailVO experimentDetailVO=experimentService.getByExperimentId(experimentId);
        Long sysEnvId=experimentDetailVO.getSysEnvId();
        SystemEnvDetailVo systemEnvDetailVo=systemEnvService.getSystemEnvInfo(userId,sysEnvId);
        String workloadName=systemEnvDetailVo.getWorkloadName().toString();
        String workloadVersion=systemEnvDetailVo.getWorkloadVersion().toString();
        List<SystemWorkload> systemWorkloadList=workloadService.getAllSystemWorkload();
        String execParams=null;
        JSONObject jsonObject=null;
        for (SystemWorkload map : systemWorkloadList) {
            if(map.getWorkloadName().equals(workloadName)&&map.getWorkloadVersion().equals(workloadVersion)){
                jsonObject= JSON.parseObject(map.getExecParams().toString());
                break;
            }
        }
        return new ResponseResult(HttpCode.OK,jsonObject);
    }

    @ApiOperation("从结果文件中获取结果")
    @PostMapping("/getResult")
    public ResponseResult getResult(@ApiParam(name = "experimentRunId", value = "实验运行id", required = true, type = "Long")
                                    @RequestParam("experimentRunId") Long experimentRunId){
        ExperimentRun experimentRun=experimentService.getExperimentByRunId(experimentRunId);
        String resultFilePath =  experimentRun.getResult();
        File resultFile = new File(resultFilePath);
        JSONObject jsonObject=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
            InputStream is = new FileInputStream(resultFile);
            jsonObject = JSONObject.parseObject(IOUtils.toString(is, "utf-8"));
            is.close();
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseResult(HttpCode.OK.getCode(), "The result was obtained successfully", true, jsonObject);
    }


    @Deprecated
    @ApiOperation("数据保存与sse推送测试")
    @PostMapping("/test")
    public ResponseResult test(@ApiParam(name = "conversationId", value = "算法与后端会话ID", required = true, type = "String")
                               @RequestParam("conversationId") String conversationId){
        String result="5143";
        //TODO 通过sse推送到前端
        if(ProcessSseEmitters.getSseEmitterByKey(conversationId)==null){
            SseEmitter emitter = new SseEmitter(0L);
            try {
                ProcessSseEmitters.addSseEmitters(conversationId,emitter);
                ProcessSseEmitters.getSseEmitterByKey(conversationId).send(result);
            }catch (Exception e){
                emitter.completeWithError(e);
            }
        }else{
            try{
                ProcessSseEmitters.getSseEmitterByKey(conversationId).send(result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  new ResponseResult(HttpCode.SERVER_ERROR.getCode(),"测试orz", false);
    }
}
