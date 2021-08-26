package com.bdilab.colosseum.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.benchmark.execute.MultiAgExecutor;
import com.bdilab.colosseum.benchmark.factory.SutConfigFactory;
import com.bdilab.colosseum.benchmark.factory.WorkloadFactory;
import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.bo.ExperimentBO_V2;
import com.bdilab.colosseum.bo.SoftwareLocationBO;
import com.bdilab.colosseum.domain.Algorithm;
import com.bdilab.colosseum.domain.Experiment;
import com.bdilab.colosseum.domain.ExperimentRun;
import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.enums.ComponentType;
import com.bdilab.colosseum.enums.ExperimentRunningStatus;
import com.bdilab.colosseum.exception.BusinessException;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.global.Context;
import com.bdilab.colosseum.mapper.*;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.ExperimentService;
import com.bdilab.colosseum.service.PipelineService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.utils.MapUtils;
import com.bdilab.colosseum.utils.NumberUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import com.bdilab.colosseum.vo.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * @ClassName ExperimentServiceImpl
 * @Author wx
 * @Date 2021/1/4 0004 9:52
 **/
@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Resource
    ExperimentMapper experimentMapper;
    @Resource
    SystemEnvMapper systemEnvMapper;
    @Resource
    ExperimentRunMapper experimentRunMapper;
    @Resource
    AlgorithmMapper algorithmMapper;
    @Resource
    PerformanceGetMethodMapper performanceGetMethodMapper;
    @Resource
    UserSutMapper userSutMapper;
    @Resource
    SystemWorkloadMapper systemWorkloadMapper;
    @Autowired
    PipelineService pipelineService;
    @Resource
    ComponentMapper componentMapper;

    @Value("${result.path}")
    private String resultPath;

    @Value("${image.map}")
    private String imageMap;

    @Value("${logo.path}")
    private String logoPath;

    /**
     * 添加实验
      * @param experimentName
     * @param description
     * @param userId
     * @param sysEnvId
     * @param allParams
     * @param blackList
     * @param whiteList
     * @param algorithmIds
     * @param metricsSetting
     * @param resultSetting
     * @return
     */
    @Override
    public int add(String experimentName, String description, Long userId, Long sysEnvId, String allParams,
                   String blackList, String whiteList, String algorithmIds, String performance,
                   String metricsSetting, String resultSetting, MultipartFile logo) {
        boolean uploadSuccess=true;
        //如果Logo为null，交个前端处理（前端使用默认Logo）
        String path="";
        if(logo!=null){
            String logoPath1=this.logoPath+userId;
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="experiment_"+UUID.randomUUID() +"."+strs[strs.length-1];
            uploadSuccess= FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        if(uploadSuccess){
            Experiment experiment = Experiment.builder()
                    .experimentName(experimentName)
                    .description(description)
                    .fkUserId(userId)
                    .fkSysEnvId(sysEnvId)
                    .allParams(allParams)
                    .blackList(blackList)
                    .whiteList(whiteList)
                    .fkAlgorithmIds(algorithmIds)
                    .performance(performance)
                    .metricsSetting(metricsSetting)
                    .resultSetting(resultSetting)
                    .logoPath(path)
                    .build();
            return experimentMapper.insertSelective(experiment);
        }
        return 0;
    }

    /**
     * 检测添加的算法是否合法
     * @param algorithmIds
     * @return
     */
    @Override
    public boolean checkAlgorithms(List<Long> algorithmIds,Long userId) {
        int count = algorithmMapper.countValidAlgorithmNums(algorithmIds, userId);
        return algorithmIds.size() == count;
    }

    /**
     * 更新实验
     * @param experimentId
     * @param description
     * @param sysEnvId
     * @param allParams
     * @param blackList
     * @param whiteList
     * @param algorithmIds
     * @param metricsSetting
     * @param resultSetting
     * @return
     */
    @Override
    public int update(Long experimentId, String description, Long sysEnvId, String allParams, String blackList, String whiteList,
                      String algorithmIds, String performance, String metricsSetting, String resultSetting,MultipartFile logo) {
        boolean uploadSuccess=true;
        String path=null;
        if(logo!=null){
            //删除之前的logo图片
            Experiment experiment = experimentMapper.selectByPrimaryKey(experimentId);
            if (!experiment.getLogoPath().equals("")){
                File file=new File(experiment.getLogoPath());
                file.delete();
            }
            Long userId=experiment.getFkUserId();
            String logoPath1=this.logoPath+userId;
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="experiment_"+UUID.randomUUID() +"."+strs[strs.length-1];
            uploadSuccess=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        Experiment experiment = Experiment.builder()
                .id(experimentId)
                .description(description)
                .fkSysEnvId(sysEnvId)
                .allParams(allParams)
                .blackList(blackList)
                .whiteList(whiteList)
                .fkAlgorithmIds(algorithmIds)
                .performance(performance)
                .metricsSetting(metricsSetting)
                .resultSetting(resultSetting)
                .logoPath(path)
                .build();
        return experimentMapper.updateByPrimaryKeySelective(experiment);
    }

    /**
     * 展示用户创建的实验列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<ExperimentVO> getList(Long userId) {
        List<ExperimentVO> experimentVOS = experimentMapper.selectByUserId(userId);
        for(ExperimentVO experimentVO:experimentVOS){
            if(!(experimentVO.getLogoPath().equals(""))){
                experimentVO.setLogoPath(imageMap+experimentVO.getLogoPath());
            }
        }
        return experimentVOS;
    }

    /**
     * 根据experimentId检查实验是否存在
     *
     * @param experimentId
     * @return
     */
    @Override
    public boolean checkByExperimentId(Long experimentId) {
        return experimentMapper.checkById(experimentId) > 0;
    }

    @Override
    public boolean checkRunningExperimentByExperimentId(Long experimentId) {
        return experimentRunMapper.checkRunningExperimentByExperimentId(experimentId) > 0;
    }

    /**
     * 根据experimentId查询返回实验详情
     *
     * @param experimentId
     * @return
     */
    @Override
    public ExperimentDetailVO getByExperimentId(Long experimentId) {
        ExperimentDetailVO experimentDetailVO = experimentMapper.selectByExperimentId(experimentId);
        ArrayList<Long> algorithmIds = new ArrayList<>();
        for (Object o : JSONArray.parseArray(experimentDetailVO.getFkAlgorithmIds())) {
            algorithmIds.add(Long.parseLong(o.toString()));
        }
        experimentDetailVO.setAlgorithmVOS(algorithmMapper.selectAgForEpDetailVOByAgIds(algorithmIds));
        experimentDetailVO.setSystemEnv(systemEnvMapper.selectBySysEnvId(experimentDetailVO.getSysEnvId()));
        return experimentDetailVO;
    }

    /**
     * 运行实验（对于暂不确定返回值的先使用void）
     *
     * @param experimentId
     */
    @Override
    @Transactional
    public Map<String, Object> run(Long experimentId, Map<String,String> execParams, Integer restrictNumber) {
        //根据实验id获取参数值、pipeline_id、实验配置参数
        //在这一步获取性能指标的值
        ExperimentBO_V2 experimentBO = experimentMapper.selectExperimentBOByExperimentId_V2(experimentId);
        // 将实验相关的参数加入到参数列表中
        ArrayList<ArrayList<String>> parameter = new ArrayList<>();
        // 添加数据库中的组件参数
        //addComponentParam(parameter,experimentBO.getParamValue());

        // 将experiment_id存入到experiment_run中
        ExperimentRun experimentRun = new ExperimentRun();
        experimentRun.setFkExperimentId(experimentId);
        experimentRunMapper.insertSelective(experimentRun);
        
        // 将conversionId传递给算法端，用于算法调用组件运行状态更新接口中，接口中实现将组件运行状态通过sse会话传递给前端进行展示
        String conversationId = UUID.randomUUID().toString();

        //添加配置参数
        addConfigParam(parameter,conversationId,restrictNumber);

        // 根据envId获取sutName 和 sutVersion
        Map<String,Object> sutNameAndSutVersion = systemEnvMapper.selectSutNameAndSutVersionByEnvId(experimentBO.getFkSysEnvId());
        String sutName = sutNameAndSutVersion.get("sut_name").toString();
        String sutVersion = sutNameAndSutVersion.get("sut_version").toString();

        String performanceFilePath = System.getProperty("user.dir") + "/file/sys/performanceFile/"+sutName+"-performance.xls";
        String performance = null;
        // 拿到性能指标xxx_min 或 xxx_max
        try {// TODO performance文件地址也要从数据库拿到
            performance = experimentBO.getPerformance() + "_"
                    + XlsUtils.readExcel(performanceFilePath,experimentBO.getPerformance(),Constant.MIN_MAX_ROW_NUMBER);
        } catch (Exception e) {
            throw new BusinessException(HttpCode.BAD_REQUEST,"从文件中获取性能指标出错",e);
        }

        //添加调优参数
        addSutParam(parameter, generateParams(experimentBO.getAllParams()),experimentBO.getBlackList(),experimentBO.getWhiteList(),performance);

        // 根据envId获取workloadName 和 WorkloadVersion
        Map<String, Object> workloadNameAndWorkloadVersion = systemEnvMapper.selectWorkloadNameAndWorkloadVersionByEnvId(experimentBO.getFkSysEnvId());
        String workloadName = workloadNameAndWorkloadVersion.get("workload_name").toString();
        String workloadVersion = workloadNameAndWorkloadVersion.get("workload_version").toString();

        //#############构造运行时环境 sutConfig + workload - START #############
        SoftwareLocationBO softwareLocationBO = systemEnvMapper.selectSoftWareLocationByEnvId(experimentBO.getFkSysEnvId());
        Map<String,Object> commonParams = null;
        Map<String,Object> contextParams = new HashMap<>();
        try {
            commonParams = MapUtils.objectToMap(softwareLocationBO);
        } catch (IllegalAccessException e) {
            throw new BusinessException(HttpCode.BAD_REQUEST,"softwareLocationBO -> commonParams出错",e);
        }
        contextParams.put("experimentRunId",experimentRun.getId());
        contextParams.put("envId",experimentBO.getFkSysEnvId());
        //这里要根据sutname + version 使用工厂得到的sutConfig
        SutConfig sutConfig = SutConfigFactory.getSutConfig(sutName, sutVersion);
        sutConfig.setPerformanceFilePath(performanceFilePath);
        //这里要根据workloadName + version 使用工厂得到的workload
        Workload workload = WorkloadFactory.getWorkload(workloadName, workloadVersion);
        workload.setSutConfig(sutConfig);
//        workload.init(execParams,commonParams,contextParams);
//        Context.getInstance().putWorkload(conversationId, workload);

        // 调用实验运行接口并获取runId
//        String runId = pipelineService.createRun(experimentBO.getPipelineId(),"pipelineName" + UUID.randomUUID(), parameter);
//        System.out.println("pipeline开始运行成功，返回runId：" + runId);

        //创建结果文件，一个实验对应一个结果文件，将contextParams和execParams保存到里面
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("experimentRunId",contextParams.get("experimentRunId"));
        jsonObject.put("envId",contextParams.get("envId"));
        JSONObject jsonObject1=new JSONObject();
        for(String key:execParams.keySet()){
            jsonObject1.put(key,execParams.get(key));
        }
        jsonObject.put("execParam",jsonObject1);
        /*jsonObject.put("result","[]");*/

        String resultFilePath =  resultPath + "result_" +experimentRun.getId() +".json";
        File resultFile = new File(resultFilePath);
        try {
            if (!resultFile.exists()) {
                resultFile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
            bw.write(jsonObject.toString());
            bw.close();
        } catch (IOException e) {
            throw new BusinessException(HttpCode.BAD_REQUEST,"创建结果文件出错",e);
        }

        contextParams.put("resultFilePath",resultFilePath);

        workload.init(execParams,commonParams,contextParams);
        Context.getInstance().putWorkload(conversationId, workload);
        //#############构造运行时环境 sutConfig + workload - END #############

        //#####################生成执行器，传入该实验的每个算法 - START ########################
        JSONArray fkAlgorithmIds = JSONArray.parseArray(experimentBO.getFkAlgorithmIds());
        List<MultiAgExecutor.ExecutorAg> executorAgs = new ArrayList<>();
        for (Object fkAlgorithmId : fkAlgorithmIds) {
            Algorithm algorithm = algorithmMapper.selectByPrimaryKey(((Integer)fkAlgorithmId).longValue());
            MultiAgExecutor.ExecutorAg executorAg = new MultiAgExecutor.ExecutorAg();
            executorAg.setId(algorithm.getId());
            executorAg.setAlgorithmName(algorithm.getAlgorithmName());
            executorAg.setParamValue(algorithm.getParamValue());
            executorAg.setPipelineId(algorithm.getPipelineId());

            String componentNamesJoinInString = pipelineService.getComponentNamesJoinInString(algorithm.getGgeditorObjectString());
            String buildInAlgorithmName = Constant.BUILD_IN_ALGORITHM.getOrDefault(componentNamesJoinInString,"NONE");

            executorAg.setBuildInAlgorithmName(buildInAlgorithmName);
            executorAgs.add(executorAg);
        }
        MultiAgExecutor executor = new MultiAgExecutor(parameter,executorAgs);
        Context.getInstance().putExecutor(conversationId,executor);
        //#####################生成执行器，传入该实验的每个算法 - END ########################

        String runId = executor.execute(pipelineService);
        if(runId==null){
            throw new BusinessException(HttpCode.BAD_REQUEST,"实验运行失败，检查Kubeflow相关环境");
        }
        experimentRun.setRunId("[\""+runId+"\"]");
        // 暂时状态为正在运行中，后续实现：组件中添加完成后调用java接口，改变数据库中的实验状态
        experimentRun.setStatus(ExperimentRunningStatus.RUNNING.getValue());
        experimentRun.setResult(resultFilePath);
        experimentRun.setStartTime(new Date());
        int update = experimentRunMapper.updateByPrimaryKeySelective(experimentRun);
        Map<String, Object> resultMap = new HashMap<>();
        if (update>0){
            resultMap.put("conversationId", conversationId);
            resultMap.put("isSucceed", true);
        }else {
            resultMap.put("conversationId", conversationId);
            resultMap.put("isSucceed", false);
        }
        return resultMap;
    }


    private void addConfigParam(ArrayList<ArrayList<String>> parameter, String conversationId, Integer restrictNumber){
        HashMap<String,Object> map = new HashMap<>();
        map.put("conversationId",conversationId);
        map.put("ip_port",Constant.IP_PORT);
        map.put("output_dir",Constant.OUTPUT_DIR);
        map.put("performance_retry_times",Constant.PERFORMANCE_RETRY_TIMES);
        map.put("report_retry_times",Constant.REPORT_RETRY_TIMES);
        map.put("report_retry_interval",Constant.REPORT_RETRY_INTERVAL);
        map.put("restrict_number",restrictNumber);
        String config_params = JSONObject.toJSONString(map);
        ArrayList<String> list = new ArrayList<>();
        list.add(Constant.CONFIG_PARAMS);
        list.add(config_params);
        parameter.add(list);
    }

    private void addSutParam(ArrayList<ArrayList<String>> parameter,String all_params,String black_list,String white_list, String performance){
        HashMap<String,String> map = new HashMap<>();
        map.put("all_params",all_params);
        map.put("black_list",(black_list==null||black_list.equals(""))?"[]":black_list.replaceAll("\"","'"));
        map.put("white_list",(white_list==null||white_list.equals(""))?"[]":white_list.replaceAll("\"","'"));
        map.put("performance",performance);

        String common_params = JSONObject.toJSONString(map);
        ArrayList<String> list = new ArrayList<>();
        list.add(Constant.COMMON_PARAMS);
        list.add(common_params);
        parameter.add(list);
    }

    /**
     * python端调用，根据experimentRunId修改数据表中experiment_run的状态，根据conversationId+sse会话传递消息给前端，根据resultFilePath读取执行结果
     *
     * @param experimentRunId
     * @param conversationId
     * @param resultFilePath
     * @return
     */
    @Override
    public boolean pushData(Long experimentRunId, String conversationId, String componentName, String resultFilePath) {
        // 根据componentName获取组件，根据type判断是否为最后一个组件
        Integer type = componentMapper.selectTypeByComponentName(componentName);
        if (!ComponentType.OPTIMAL_PARAMETER_SEARCH.getType().equals(type)) {
            System.out.println(componentName+"运行成功");
//            ProcessSseEmitters.sendEvent(conversationId, "组件 " + componentName + " 已运行成功");
        } else {
            System.out.println(componentName+"运行成功");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("status", "finished");
            // 根据运行结果路径读取文件获取运行结果
            System.out.println("结果文件存储路径：" + resultFilePath);

            // 根据experimentRunId即experiment_run的主键id查找experiment_run并更新
            ExperimentRun experimentRun = experimentRunMapper.selectByPrimaryKey(experimentRunId);
            experimentRun.setStatus(ExperimentRunningStatus.RUNNINGSUCCESS.getValue());
            // 直接将文件地址存在后端
            experimentRun.setResult(resultFilePath);
            experimentRun.setEndTime(new Date());
            experimentRunMapper.updateByPrimaryKey(experimentRun);

            ResponseResult responseResult = new ResponseResult(HttpCode.OK.getCode(), componentName + "运行完毕，整个流程结束", true, resultMap);
            System.out.println(responseResult.toString());
//            ProcessSseEmitters.sendEvent(conversationId, responseResult);
//            // 结束会话
//            ProcessSseEmitters.getSseEmitterByKey(conversationId).complete();
//            // 清除sse对象
//            ProcessSseEmitters.removeSseEmitterByKey(conversationId);
        }
        return true;
    }

    /**
     * 将待调优系统参数调整为算法侧可识别的参数格式
     * @param params
     * @return
     */
    private String generateParams(String params) {
        StringBuilder result = new StringBuilder();
        // 将params转为json数组
        JSONArray jsonArray = JSONArray.parseArray(params);
        // 构造参数{'a':['int', [0, 50], 0],'b':['enum',['on','off'],'on'],'c':['float',[0.1,0.2],0.1]}
        result.append("{");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.getString(i));
            // 参数间使用,分隔
            if (jsonObject.get("validValue")!=null) {
                result.append("'").append(jsonObject.get("name")).append("':['")
                        .append(transType(jsonObject.get("type").toString())).append("',")
                        .append("['").append(jsonObject.get("validValue").toString().replaceAll(";", "','")).append("']").append(",")
                        .append("'").append(jsonObject.get("defaultValue")).append("'").append("],");
            } else {
                result.append("\'").append(jsonObject.get("name")).append("\':[\'")
                        .append(transType(jsonObject.get("type").toString())).append("\',[")
                        .append(jsonObject.get("minValue")).append(",")
                        .append(jsonObject.get("maxValue")).append("],")
                        .append(jsonObject.get("defaultValue")).append("],");
            }
        }
        result.replace(result.length()-1, result.length(), "}");
        return result.toString();
    }

    /**
     * 转换java类型为算法可识别的类型，暂时这么处理。后续上传参数文件的时候需要调整为符合规定的格式
     * @param oldType
     * @return
     */
    private String transType(String oldType) {
        if ("Integer".equals(oldType)) {
           return "int";
        } else if ("Float".equals(oldType)) {
           return "float";
        } else if ("Boolean".equals(oldType) || "Enumeration".equals(oldType)) {
           return "enum";
        }
        return "";
    }

//    public static void main(String[] args) {
//        ExperimentServiceImpl e = new ExperimentServiceImpl();
//        System.out.println(e.generateParams("[{\"name\":\"admin_port\",\"type\":\"Integer\",\"minValue\":\"0\",\"maxValue\":\"65535\",\"validValue\":null,\"defaultValue\":\"5\"},{\"name\":\"audit_log_buffer_size\",\"type\":\"Integer\",\"minValue\":\"4096\",\"maxValue\":\"18446744073709547520\",\"validValue\":null,\"defaultValue\":\"4099\"}," +
//                "{\"name\":\"innodb_adaptive_hash_index\",\"type\":\"Boolean\",\"minValue\":null,\"maxValue\":null,\"validValue\":[\"on\",\"off\"],\"defaultValue\":\"on\"}]"));
//    }

    /**
     * 查看历史实验结果列表
     * @return
     */
    @Override
    public List<JSONObject> getResultList(Long experimentId) {
//        List<ExperimentRun> experimentRunList = experimentRunMapper.selectByExperimentId(experimentId);
//        for (ExperimentRun experimentRun : experimentRunList) {
//            if (ExperimentRunningStatus.RUNNING.getValue() == experimentRun.getStatus()) {
//                // 如果结果失败，则更新数据库
//                if (pipelineService.checkFailedStatus(experimentRun.getRunId())); {
//                    experimentRun.setStatus(ExperimentRunningStatus.RUNNINGFAIL.getValue());
//                    experimentRun.setEndTime(new Date());
//                    experimentRunMapper.updateByPrimaryKeySelective(experimentRun);
//                }
//            }
//        }
//        return experimentRunMapper.selectByExperimentIdAndUserId(experimentId);
        System.out.println(experimentId);
        List<ExperimentRun> experimentRuns = experimentRunMapper.selectByExperimentId(experimentId);
        System.out.println(experimentRuns.size());
        List<JSONObject> list=new ArrayList<>();
        SystemWorkload systemWorkload=null;
        for(ExperimentRun experimentRun:experimentRuns){
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("id",experimentRun.getId());
            jsonObject1.put("runId",experimentRun.getRunId());

            //获取多个算法中的最最优解法
            String resultFilePath =  experimentRun.getResult();
            File resultFile = new File(resultFilePath);
            String s=null;
            JSONObject js=null;
            String target=null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
                InputStream is = new FileInputStream(resultFile);
                js = JSONObject.parseObject(IOUtils.toString(is, "utf-8"));
                //1. 获取到性能名
                Experiment experiment=experimentMapper.selectByPrimaryKey(experimentRun.getFkExperimentId());
                String performance=experiment.getPerformance();
                SystemEnvVO systemEnvVO = systemEnvMapper.selectBySysEnvId(experiment.getFkSysEnvId());
                //2. 获取到性能值是max 或者是 min（max代表该值越大越好，min代表该值是越小越好）,保存再变量target中
                systemWorkload=systemWorkloadMapper.selectByNameAndVersion(systemEnvVO.getWorkloadName(),systemEnvVO.getWorkloadVersion());
                String performanceFilePath=System.getProperty("user.dir")+systemWorkload.getPerformance();
                if (performanceFilePath!=null){
                    List<PerformanceVo> performanceFromXls = XlsUtils.getPerformanceFromXls(performanceFilePath);
                    for(PerformanceVo pv:performanceFromXls){
                        if(pv.getActualPerformance().equalsIgnoreCase(performance)){
                            target=pv.getMinOrMax();
                            break;
                        }
                    }
                }
                List<Float> list1=new ArrayList<>();
                for(String k:js.keySet()){
                    if(!k.equals("experimentRunId")&&!k.equals("envId")&&!k.equals("execParam")){
                        list1.add(Float.parseFloat(js.getJSONObject(k).getJSONObject("data").get(performance).toString()));
                    }
                }
                if(target.equals("max")){
                    s=performance+"="+Collections.max(list1);
                }
                if(target.equals("min")){
                    s=performance+"="+Collections.min(list1);
                }

                is.close();
                br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            jsonObject1.put("result",s);
            jsonObject1.put("status",experimentRun.getStatus());
            jsonObject1.put("start time",experimentRun.getStartTime());
            jsonObject1.put("end time",experimentRun.getEndTime());
            list.add(jsonObject1);
        }
        return list;
    }

    /**
     * 根据resultId检查实验结果是否存在
     *
     * @param resultId
     * @return
     */
    @Override
    public boolean checkByResultId(Long resultId) {
        return experimentRunMapper.checkById(resultId) > 0;
    }

    /**
     * 根据resultId返回实验结果详情
     *
     * @param resultId
     *
     * @return
     */
    @Override
    public ExperimentResultDetailVO getResultDetail(Long resultId) {
        ExperimentRun experimentRun = experimentRunMapper.selectByPrimaryKey(resultId);
        // 根据获取的结果status，如果是正在运行中，调用kubeflow的接口去查看是否为组件运行失败，如果是更新数据库
        if (ExperimentRunningStatus.RUNNING.getValue().equals(experimentRun.getStatus())) {
            // 如果结果失败，则更新数据库
           if (pipelineService.checkFailedStatus(experimentRun.getRunId())) {
                experimentRun.setStatus(ExperimentRunningStatus.RUNNINGFAIL.getValue());
                experimentRun.setEndTime(new Date());
                experimentRunMapper.updateByPrimaryKeySelective(experimentRun);
            }
        }
        ExperimentResultDetailVO experimentResultDetailVO = experimentRunMapper.selectByResultId(resultId);
        // 对csv文件的读取
        if (experimentResultDetailVO.getResult() != null) {
            String filePath = experimentResultDetailVO.getResult();
            // 读取csv文件
            List<List<String>> result = XlsUtils.getCellFromCSV(filePath);
            experimentResultDetailVO.setResult(result.toString());
        }
        experimentResultDetailVO.setExperiment(experimentMapper.selectByExperimentId(experimentResultDetailVO.getExperimentId()));
        return experimentResultDetailVO;
    }

//    @Override
//    public boolean terminate(Long resultId) {
//        // 根据resultId获取runId
//        ExperimentRun experimentRun = experimentRunMapper.selectByPrimaryKey(resultId);
//        // 更新实验结果中status
//        experimentRun.setStatus(ExperimentRunningStatus.RUNNINGFAIL.getValue());
//        experimentRun.setEndTime(new Date());
//        return experimentRunMapper.updateByPrimaryKeySelective(experimentRun) > 0;
//    }

    /**
     * 根据算法端传来的paramList,performance,再根据找到对应的workload进行调优实验
     * @param workload
     * @param performance
     * @param paramList
     * @return
     */
    @Override
    public String getPerformance(Workload workload, String paramList, String performance) {
        Map<String,Object> params = new HashMap<>();
        params.put("paramList",paramList);
        params.put("performance",performance);
        workload.start(params);
        String result = workload.getResult();
        System.out.println("result="+result);
        workload.clean();
        return NumberUtils.isNumber(result)?result: null;
    }

    /**
     * 根据实验ID删除实验
     *
     * @param experimentId
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public boolean delete(Long experimentId) {
        // 根据实验ID获取实验运行记录
        List<ExperimentRun> experimentRuns = experimentRunMapper.selectByExperimentId(experimentId);
        // 根据runId删除kubeflow中的实验
        for (ExperimentRun experimentRun : experimentRuns) {
            String[] strs=experimentRun.getRunId().replaceAll("(\"|\\[|])","")
                    .split(",");
            for(String str:strs){
                pipelineService.deleteRunById(str);
            }
        }
        // 删除实验运行记录以及实验运行结果文件
        int delete = experimentRunMapper.deleteByExperimentId(experimentId);
        if (delete < 0) {
            return false;
        }
        //删除结果文件
        for(ExperimentRun experimentRun : experimentRuns) {
            String resultFilePath =  resultPath + "result_"+experimentRun.getId() +".json";
            File file = new File(resultFilePath);
            if (!file.exists()) {
                System.out.println("Failed to delete file:" + resultFilePath + "does not exist！");
                return false;
            } else {
                file.delete();
            }
        }
        //删除该实验
        return experimentMapper.deleteByPrimaryKey(experimentId) > 0;
    }

    /**
     * 根据runID来更新实验运行结果
     * @param conversationId
     * @param data
     * @return
     */
    @Override
    public int saveResult(String conversationId,String data){
        ExperimentRun experimentRun=new ExperimentRun();
        experimentRun.setRunId(conversationId);
        experimentRun.setResult(data);
        experimentRun.setStatus(ExperimentRunningStatus.RUNNINGSUCCESS.getValue());
        experimentRun.setEndTime(new Date());
        return experimentRunMapper.updateResultByRunId(experimentRun);
    }
    /**
     * 根据实验ID获取实验运行的最新runId
     *
     * @param experimentId
     * @return
     */
    @Override
    public String getNewestRunIdbyExperimentId(Long experimentId){
        List<String> list=experimentRunMapper.selectRunIdByExperimentId(experimentId);
        return list.get(0);
    }

    @Override
    public void updateRunIdINExperimentRun(Long experimentRunId, String runId) {
        String runIds = experimentRunMapper.selectByPrimaryKey(experimentRunId).getRunId();
        JSONArray jsonArray = JSONObject.parseArray(runIds);
        jsonArray.add(runId);
        ExperimentRun experimentRun = new ExperimentRun();
        experimentRun.setId(experimentRunId);
        experimentRun.setRunId(jsonArray.toJSONString());
        experimentRunMapper.updateByPrimaryKeySelective(experimentRun);
    }

    @Override
    public void updateStatusINExperimentRun(Long experimentRunId, ExperimentRunningStatus status, boolean isEnd) {
        ExperimentRun experimentRun = new ExperimentRun();
        experimentRun.setId(experimentRunId);
        experimentRun.setStatus(status.getValue());
        if(isEnd){
            experimentRun.setEndTime(new Date());
        }
        experimentRunMapper.updateByPrimaryKeySelective(experimentRun);
    }

    @Override
    public ExperimentRun getExperimentByRunId(Long experimentRunId){
        return experimentRunMapper.selectByPrimaryKey(experimentRunId);
    }

    @Override
    public List<Experiment> getExperimentByuserId(Long userId){
        return experimentMapper.selectAllExperimentsByUserId(userId);
    }
}
