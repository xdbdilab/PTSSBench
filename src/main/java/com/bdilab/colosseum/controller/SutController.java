package com.bdilab.colosseum.controller;

import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.domain.SystemSut;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.HardwareService;
import com.bdilab.colosseum.service.SutService;
import com.bdilab.colosseum.service.SystemEnvService;
import com.bdilab.colosseum.service.WorkloadService;
import com.bdilab.colosseum.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 16:43
 **/
@Api(tags = "待调优系统管理API")
@RestController
@RequestMapping("/sut")
public class SutController {

    private static final Logger log = LoggerFactory.getLogger(SutController.class);
    @Resource
    SystemEnvService systemEnvService;
    @Resource
    SutService sutService;
    @Resource
    HardwareService hardwareService;
    @Resource
    WorkloadService workloadService;

    @ApiOperation("获取系统环境列表")
    @GetMapping("/getSystemEnvList")
    public ResponseResult getSystemEnvList(HttpSession session) throws Exception{
        Long userId = (Long) session.getAttribute("user_id");
        List<SystemEnv> systemEnvList= systemEnvService.getSystemEnvList(userId);
        return new ResponseResult(HttpCode.OK,systemEnvList);
    }

    @ApiOperation("获取系统环境信息")
    @GetMapping("/{systemEnvId}/getSystemEnvInfo")
    public ResponseResult getSystemEnvInfo(HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId) throws Exception{
        Long userId = (Long) session.getAttribute("user_id");

        SystemEnvDetailVo systemEnvDetail=systemEnvService.getSystemEnvInfo(userId,systemEnvId);
        return new ResponseResult(HttpCode.OK,systemEnvDetail);
    }

    @ApiOperation("添加系统环境")
    @PostMapping("/addSystemEnv")
    public ResponseResult addSystemEnv(
            HttpSession session,
            @ApiParam(name = "envName", value = "环境名", type = "String") @RequestParam String envName,
            @ApiParam(name = "envDesc", value = "环境描述", type = "String") @RequestParam(required = false) String envDesc,
            @ApiParam(name = "hardwareName", value = "硬件环境名", type = "String") @RequestParam String hardwareName,
            @ApiParam(name = "hardwareDesc", value = "硬件环境描述", type = "String") @RequestParam(required = false) String hardwareDesc,
            @ApiParam(name = "hardType", value = "硬件环境配置类型", type = "Byte") @RequestParam Byte hardType,
            @ApiParam(name = "hardwareIp", value = "硬件环境IP",  type = "Long") @RequestParam String hardwareIp,
            @ApiParam(name = "hardUsername", value = "硬件环境用户名", type = "String") @RequestParam String hardUsername,
            @ApiParam(name = "hardPwd", value = "硬件环境密码",  type = "String") @RequestParam String hardPwd,
            @ApiParam(name = "sutName", value = "待调优系统名", type = "String") @RequestParam String sutName,
            @ApiParam(name = "sutVersion", value = "待调优系统版本",  type = "String") @RequestParam String sutVersion,
            @ApiParam(name = "sutInstallPath", value = "待调优系统安装路径",  type = "String") @RequestParam String sutInstallPath,
            @ApiParam(name = "sutTestCmd", value = "待调优系统可用性测试shell命令串",  type = "String") @RequestParam(required = false) String sutTestCmd,
            @ApiParam(name = "parameters", value = "待调优系统参数",  type = "String") @RequestParam String parameters,
            @ApiParam(name = "workloadName", value = "工作负载名", type = "String") @RequestParam String workloadName,
            @ApiParam(name = "workloadVersion", value = "工作负载版本",  type = "String") @RequestParam String workloadVersion,
            @ApiParam(name = "workloadInstallPath", value = "工作负载安装路径",  type = "String") @RequestParam String workloadInstallPath,
            @ApiParam(name = "workloadTestCmd", value = "工作负载可用性测试shell命令串",  type = "String") @RequestParam(required = false) String workloadTestCmd,
            @ApiParam(name = "performances", value = "工作负载的性能指标",  type = "String") @RequestParam String performances,
            @ApiParam(name = "logo", value = "环境Logo")@RequestParam(required = false) MultipartFile logo
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        SystemEnv systemEnv= systemEnvService.addSystemEnv(userId,envName,envDesc,hardwareName,hardwareDesc,
                hardType,hardwareIp,hardUsername,hardPwd,sutName,sutVersion,sutInstallPath,sutTestCmd,
                parameters,workloadName,workloadVersion, workloadInstallPath, workloadTestCmd,performances,logo);
        if (systemEnv == null){
            return new ResponseResult(HttpCode.SERVER_ERROR,"系统环境添加失败，请检查配置后重试");
        }
        return new ResponseResult(HttpCode.OK,systemEnv);
    }

    @ApiOperation("编辑系统环境")
    @PostMapping("/{systemEnvId}/updateSystemEnv")
    public ResponseResult updateSystemEnv(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId,
            @ApiParam(name = "envName", value = "环境名", type = "String") @RequestParam String envName,
            @ApiParam(name = "envDesc", value = "环境描述", type = "String") @RequestParam(required = false) String envDesc,
            @ApiParam(name = "hardwareName", value = "硬件环境名", type = "String") @RequestParam String hardwareName,
            @ApiParam(name = "hardwareDesc", value = "硬件环境描述", type = "String") @RequestParam(required = false) String hardwareDesc,
            @ApiParam(name = "hardType", value = "硬件环境配置类型", type = "Byte") @RequestParam Byte hardType,
            @ApiParam(name = "hardwareIp", value = "硬件环境IP",  type = "Long") @RequestParam String hardwareIp,
            @ApiParam(name = "hardUsername", value = "硬件环境用户名", type = "String") @RequestParam String hardUsername,
            @ApiParam(name = "hardPwd", value = "硬件环境密码",  type = "String") @RequestParam String hardPwd,
            @ApiParam(name = "sutName", value = "待调优系统名", type = "String") @RequestParam String sutName,
            @ApiParam(name = "sutVersion", value = "待调优系统版本",  type = "String") @RequestParam String sutVersion,
            @ApiParam(name = "sutInstallPath", value = "待调优系统安装路径",  type = "String") @RequestParam String sutInstallPath,
            @ApiParam(name = "sutTestCmd", value = "待调优系统可用性测试shell命令串",  type = "String") @RequestParam(required = false) String sutTestCmd,
            @ApiParam(name = "parameters", value = "待调优系统参数",  type = "String") @RequestParam String parameters,
            @ApiParam(name = "workloadName", value = "工作负载名", type = "String") @RequestParam String workloadName,
            @ApiParam(name = "workloadVersion", value = "工作负载版本",  type = "String") @RequestParam String workloadVersion,
            @ApiParam(name = "workloadInstallPath", value = "工作负载安装路径",  type = "String") @RequestParam String workloadInstallPath,
            @ApiParam(name = "workloadTestCmd", value = "工作负载可用性测试shell命令串",  type = "String") @RequestParam(required = false) String workloadTestCmd,
            @ApiParam(name = "execParams", value = "执行参数", type = "String") @RequestParam(required = false) String execParams,
            @ApiParam(name = "performances", value = "工作负载的性能指标",  type = "String") @RequestParam String performances,
            @ApiParam(name = "logo", value = "环境Logo")@RequestParam(required = false) MultipartFile logo
    ) {
        //当前用户的用户信息后续可能需要从redis中读取（待修改）
        Long userId = (Long) session.getAttribute("user_id");

        SystemEnv systemEnv= null;
        try {
            systemEnv = systemEnvService.updateSystemEnv(userId,systemEnvId,envName,envDesc,hardwareName,hardwareDesc,hardType,hardwareIp,hardUsername,hardPwd,sutName,sutVersion,sutInstallPath,sutTestCmd,parameters,workloadName,workloadVersion, workloadInstallPath, workloadTestCmd,performances,logo);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            log.error("环境修改失败");
            return new ResponseResult(HttpCode.SERVER_ERROR, "环境修改失败");
        }
        return new ResponseResult(HttpCode.OK,systemEnv);
    }

    @ApiOperation("删除系统环境")
    @DeleteMapping("/{systemEnvId}/delete")
    public ResponseResult deleteSystemEnv(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId

    ) {
        //当前用户的用户信息后续可能需要从redis中读取（待修改）
        Long userId = (Long) session.getAttribute("user_id");

        try {
            systemEnvService.deleteSystemEnv(userId,systemEnvId);
        }catch (Exception e){
            log.error("删除环境失败");
            e.printStackTrace();
            return new ResponseResult(HttpCode.SERVER_ERROR,"环境删除失败");
        }
        return new ResponseResult(HttpCode.OK,null);
    }

    @ApiOperation("测试硬件环境(桥接式)")
    @GetMapping("/testHardwrd")
    public ResponseResult testHardware(
            @ApiParam(name = "hardwareIp", value = "硬件环境IP",  type = "Long") @RequestParam String hardwareIp,
            @ApiParam(name = "hardUsername", value = "硬件环境用户名", type = "String") @RequestParam String hardUsername,
            @ApiParam(name = "hardPwd", value = "硬件环境密码",  type = "String") @RequestParam String hardPwd) {
        boolean success = hardwareService.testConnect(hardwareIp,hardUsername,hardPwd);
        return new ResponseResult(HttpCode.OK,success);
    }

    @ApiOperation("获取系统的待调优系统库")
    @GetMapping("/getSystemSutList")
    public ResponseResult getSystemSutList() {
        List<SystemSutVo> systemSutVoList= sutService.getSystemSutList();
        return new ResponseResult(HttpCode.OK,systemSutVoList);
    }

    @ApiOperation("测试待调优系统(桥接式)")
    @GetMapping("/testSut")
    public ResponseResult testSut(
            @ApiParam(name = "custom", value = "是否为未支持的待调优系统",  type = "boolean") @RequestParam boolean custom,
            @ApiParam(name = "hardwareIp", value = "硬件环境IP",  type = "Long") @RequestParam String hardwareIp,
            @ApiParam(name = "hardUsername", value = "硬件环境用户名", type = "String") @RequestParam String hardUsername,
            @ApiParam(name = "hardPwd", value = "硬件环境密码",  type = "String") @RequestParam String hardPwd,
            @ApiParam(name = "sutName", value = "待调优系统名", type = "String") @RequestParam String sutName,
            @ApiParam(name = "sutVersion", value = "待调优系统版本",  type = "String") @RequestParam String sutVersion,
            @ApiParam(name = "sutInstallPath", value = "待调优系统安装路径",  type = "String") @RequestParam String sutInstallPath,
            @ApiParam(name = "testCmd", value = "待调优系统可用性测试shell命令串",  type = "String") @RequestParam(required = false) String testCmd
    ) {
        boolean success = sutService.testSut(custom,hardwareIp,hardUsername,hardPwd,sutName,sutVersion,sutInstallPath,testCmd);
        return new ResponseResult(HttpCode.OK,success);
    }

    @ApiOperation("获取系统的工作负载库")
    @GetMapping("/getSystemWorkloadList")
    public ResponseResult getSystemWorkloadList() {
        List<SystemWorkloadVo> systemWorkloadVoList= workloadService.getSystemWorkloadList();
        return new ResponseResult(HttpCode.OK,systemWorkloadVoList);
    }

    @ApiOperation("测试工作负载（桥接式）")
    @GetMapping("/testWorkload")
    public ResponseResult testWorkload(
            @ApiParam(name = "hardwareIp", value = "硬件环境IP",  type = "Long") @RequestParam String hardwareIp,
            @ApiParam(name = "hardUsername", value = "硬件环境用户名", type = "String") @RequestParam String hardUsername,
            @ApiParam(name = "hardPwd", value = "硬件环境密码",  type = "String") @RequestParam String hardPwd,
            @ApiParam(name = "workloadName", value = "工作负载名", type = "String") @RequestParam String workloadName,
            @ApiParam(name = "workloadVersion", value = "工作负载版本",  type = "String") @RequestParam String workloadVersion,
            @ApiParam(name = "workloadInstallPath", value = "工作负载安装路径",  type = "String") @RequestParam String workloadInstallPath,
            @ApiParam(name = "testCmd", value = "工作负载可用性测试shell命令串",  type = "String") @RequestParam(required = false) String testCmd
    ) {
        Map<String,Object> result = null;
        try {
            result = workloadService.testWorkload(hardwareIp,hardUsername,hardPwd,workloadName,workloadVersion,workloadInstallPath,testCmd);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.NOT_FOUND,null);
        }
        return new ResponseResult(HttpCode.OK,result);
    }

    @ApiOperation("获取指定环境的默认可选参数集合")
    @GetMapping("/{systemEnvId}/getParamsDefault")
    public ResponseResult getSystemEnvParamsDefault(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        List<ParameterVo> parameterVos = null;
        try {
            parameterVos = sutService.getSystemEnvParamsDefault(userId,systemEnvId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR, e.getMessage());
        }
        return new ResponseResult(HttpCode.OK, parameterVos);
    }

    @ApiOperation("获取指定环境的当前参数集合")
    @GetMapping("/{systemEnvId}/getParams")
    public ResponseResult getSystemEnvParams(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        List<ParameterVo> parameterVos = null;
        try {
            parameterVos = sutService.getSystemEnvParams(userId,systemEnvId);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR, e.getMessage());
        }
        return new ResponseResult(HttpCode.OK, parameterVos);
    }

    @ApiOperation("配置指定环境的参数")
    @PostMapping("/{systemEnvId}/saveParams")
    public ResponseResult saveSystemEnvParams(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId,
            @ApiParam(name = "params", value = "环境参数集合",  type = "String") @RequestParam String params
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        try {
            sutService.saveSystemEnvParams(userId,systemEnvId,params);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.OK,null);
    }



    @ApiOperation("上传参数配置文件")
    @PostMapping("/{systemEnvId}/uploadParamFile")
    public ResponseResult uploadSystemEnvParamFile(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId,
            @ApiParam(name = "paramFile", value = "参数配置文件",  type = "MultipartFile") @RequestParam MultipartFile paramFile) {
        Long userId = (Long) session.getAttribute("user_id");
        try {
            int upload = sutService.uploadSystemEnvParamFile(userId,systemEnvId,paramFile);
            if (upload>0){
                return new ResponseResult(HttpCode.OK,null);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.SERVER_ERROR,"上传失败");
    }

    @ApiOperation("获取指定环境可选的默认性能指标集合")
    @GetMapping("/{systemEnvId}/getPerformanceDefault")
    public ResponseResult getSystemEnvPerformanceDefault(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        String systemEnvPerformances= null;
        try {
            systemEnvPerformances = workloadService.getSystemEnvPerformanceDefault(userId,systemEnvId);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.OK,systemEnvPerformances);
    }

    @ApiOperation("获取指定环境的性能指标集合")
    @GetMapping("/{systemEnvId}/getPerformance")
    public ResponseResult getSystemEnvPerformance(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        String systemEnvPerformances= null;
        try {
            systemEnvPerformances = workloadService.getSystemEnvPerformance(userId,systemEnvId);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.OK,systemEnvPerformances);
    }

    @ApiOperation("配置指定环境的性能指标")
    @PostMapping("/{systemEnvId}/savePerformance")
    public ResponseResult saveSystemEnvPerformance(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId,
            @ApiParam(name = "performances", value = "环境性能指标集合",  type = "String") @RequestParam String performances
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        try {
            workloadService.saveSystemEnvPerformance(userId,systemEnvId,performances);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.OK,null);
    }

    @ApiOperation("自定义指定环境的可选性能指标集合")
    @PostMapping("/{systemEnvId}/setPerformanceDefault")
    public ResponseResult setPerformanceDefault(
            HttpSession session,
            @ApiParam(name = "systemEnvId", value = "环境id",  type = "Long") @PathVariable(value = "systemEnvId") Long systemEnvId,
            @ApiParam(name = "performances", value = "环境性能指标集合",  type = "String") @RequestParam String performances
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        try {
            workloadService.setPerformanceDefault(userId, systemEnvId,performances);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
        return new ResponseResult(HttpCode.OK,null);
    }

    @ApiOperation("创建环境时通过sutName和sutVersion从对应的参数文件中获取到参数默认列表")
    @PostMapping("/getParameterListDefault")
    public  ResponseResult getParameterListDefault(
        @ApiParam(name="sutName",value = "sut名字",type = "String") @RequestParam String sutName,
        @ApiParam(name="sutVersion",value = "sut版本",type = "String") @RequestParam String sutVersion,
        HttpSession session
    ){
        try {
            List<ParameterVo> parameterVos=sutService.getParameterListDefault(sutName,sutVersion);
            return new ResponseResult(HttpCode.OK,parameterVos);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
    }

    @ApiOperation("创建环境时通过sutName和sutVersion获取到可支持的工作负载名和版本号")
    @PostMapping("/getWorkloads")
    public  ResponseResult getWorkloads(
            @ApiParam(name="sutName",value = "sut名字",type = "String") @RequestParam String sutName,
            @ApiParam(name="sutVersion",value = "sut版本",type = "String") @RequestParam String sutVersion,
            HttpSession session
    ){
        List<SystemSut> systemSuts=sutService.getAllSystemSut();
        String workloads=null;
        for (SystemSut systemSut : systemSuts) {
            if(systemSut.getSutName().equals(sutName)
                    &&systemSut.getSutVersion().equals(sutVersion)){
                workloads=systemSut.getWorkloads().toString();
                break;
            }
        }
        List<SystemWorkloadVo> systemWorkloadVos=workloadService.getWorkloads(workloads);
        return new ResponseResult(HttpCode.OK,systemWorkloadVos);
    }

    @ApiOperation("创建环境时通过workloadName和workloadVersion从对应的性能文件中获取到性能默认列表")
    @PostMapping("/getPerformanceListDefault")
    public  ResponseResult getPerformanceListDefault(
            @ApiParam(name="workloadName",value = "workload名字",type = "String") @RequestParam String workloadName,
            @ApiParam(name="workloadVersion",value = "workload版本",type = "String") @RequestParam String workloadVersion,
            HttpSession session
    ){
        try {
            List<PerformanceVo> performanceVos=workloadService.getPerformanceListDefault(workloadName,workloadVersion);
            return new ResponseResult(HttpCode.OK,performanceVos);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
        }
    }
//    @ApiOperation("创建环境时通过workloadName和workloadVersion从对应的性能文件中获取到性能列表")
//    @PostMapping("/getPerformanceListDefault")
//    public  ResponseResult getPerformanceListDefault(
//            @ApiParam(name="workloadName",value = "workload名字",type = "String") @RequestParam String workloadName,
//            @ApiParam(name="workloadVersion",value = "workload版本",type = "String") @RequestParam String workloadVersion,
//            HttpSession session
//    ){
//        try {
//            List<String> performance=workloadService.getPerformanceListDefault(workloadName,workloadVersion);
//            return new ResponseResult(HttpCode.OK,performance);
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error(e.getMessage());
//            return new ResponseResult(HttpCode.SERVER_ERROR,e.getMessage());
//        }
//    }
}
