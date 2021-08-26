package com.bdilab.colosseum.controller;

import com.alibaba.fastjson.JSONArray;
import com.bdilab.colosseum.domain.ComponentParam;
import com.bdilab.colosseum.domain.SystemSut;
import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.ComponentService;
import com.bdilab.colosseum.service.SutService;
import com.bdilab.colosseum.service.WorkloadService;
import com.bdilab.colosseum.vo.SystemSutVo;
import com.bdilab.colosseum.vo.SystemWorkloadVo;
import com.google.gson.JsonArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;

@Api(tags = "管理员模块API")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Resource
    SutService sutService;
    @Resource
    ComponentService componentService;
    @Resource
    WorkloadService workloadService;

    @ApiOperation("添加系统的待调优系统")
    @PostMapping("/addSystemSut")
    public ResponseResult addSystemSut(HttpSession session,
                                       @ApiParam(name = "sutName", value = "待调优系统名", type = "String") @RequestParam String sutName,
                                       @ApiParam(name = "sutVersion", value = "待调优系统版本",  type = "String") @RequestParam String sutVersion,
                                       @ApiParam(name = "sutParameterFile", value = "参数配置文件",  type = "MultipartFile") @RequestParam MultipartFile  sutParameterFile,
                                       @ApiParam(name = "sutTestCmd", value = "待调优系统可用性测试shell命令串",  type = "String") @RequestParam String sutTestCmd,
                                       @ApiParam(name = "workloads", value = "可支持该待调优系统的工作负载",  type = "String") @RequestParam String workloads,
                                       @RequestParam(required = false) @ApiParam(value = "待调优系统logo")MultipartFile logo
                                       ){
            SystemSut systemSut=sutService.addSystemSut(sutName,sutVersion,sutParameterFile,sutTestCmd,workloads,logo);
            if(systemSut==null){
                return new ResponseResult(HttpCode.SERVER_ERROR,"系统待调优系统添加失败，请检查配置后重试");
            }else{
                return new ResponseResult(HttpCode.OK,systemSut);
            }
    }

    @ApiOperation("获取系统所有的待调优系统")
    @GetMapping("/getAllSystemSut")
    public ResponseResult getAllSystemSut() {
        List<SystemSut> systemSutList= sutService.getAllSystemSut();
        return new ResponseResult(HttpCode.OK,systemSutList);
    }

    @ApiOperation("获取系统所有的工作负载")
    @GetMapping("/getAllSystemWorkload")
    public ResponseResult getAllSystemWorkload() {
        List<SystemWorkload> systemWorkloadVoList= workloadService.getAllSystemWorkload();
        return new ResponseResult(HttpCode.OK,systemWorkloadVoList);
    }

    @ApiOperation("编辑系统的待调优系统")
    @PostMapping("/{systemSutId}/updateSystemSut")
    public ResponseResult updateSystemSut(HttpSession session,
                                          @ApiParam(name = "systemSutId", value = "待调优系统id",  type = "Long") @PathVariable(value = "systemSutId") Long systemSutId,
                                          @ApiParam(name = "sutName", value = "待调优系统名", type = "String") @RequestParam(required = false) String sutName,
                                          @ApiParam(name = "sutVersion", value = "待调优系统版本",  type = "String") @RequestParam(required = false) String sutVersion,
                                          @ApiParam(name = "sutParameterFile", value = "参数文件",  type = "MultipartFile") @RequestParam(required = false) MultipartFile  sutParameterFile,
                                          @ApiParam(name = "sutTestCmd", value = "待调优系统可用性测试shell命令串",  type = "String") @RequestParam(required = false) String sutTestCmd,
                                          @ApiParam(name = "workloads", value = "可支持该待调优系统的工作负载",  type = "String") @RequestParam(required = false) String workloads,
                                          @RequestParam(required = false) @ApiParam(value = "待调优系统logo")MultipartFile logo
    ){
        SystemSut systemSut=sutService.updateSystemSut(systemSutId,sutName,sutVersion,sutParameterFile,sutTestCmd,logo,workloads);
        if(systemSut==null){
            return new ResponseResult(HttpCode.SERVER_ERROR,"编辑系统待调优系统失败，请检查配置后重试");
        }else{
            return new ResponseResult(HttpCode.OK,systemSut);
        }
    }

    @ApiOperation("删除系统的待调优系统")
    @PostMapping("/{systemSutId}/deleteSystemSut")
    public ResponseResult deleteSystemSut(HttpSession session,
                                          @ApiParam(name = "systemSutId", value = "待调优系统id",  type = "Long") @PathVariable(value = "systemSutId") Long systemSutId){
        int delete=sutService.deleteSystemSut(systemSutId);
        if(delete>0){
            return new ResponseResult(HttpCode.OK,"删除系统待调优系统成功");
        }else{
            return new ResponseResult(HttpCode.SERVER_ERROR,"删除系统待调优系统失败");
        }
    }

    @ResponseBody
    @ApiOperation("创建组件")
    @RequestMapping(value = "/component/createComponent", method = RequestMethod.POST)
    public ResponseResult createComponent(@RequestParam @ApiParam(value = "组件名称")String name,
                                          @RequestParam @ApiParam(value = "组件类型")byte type,
                                          @RequestParam @ApiParam(value = "组件描述")String desc,
                                          @RequestParam @ApiParam(value = "组件镜像名称")String image,
                                          @RequestParam(required = false) @ApiParam(value = "组件参数列表")String param,
                                          @RequestParam @ApiParam(value = "组件文件地址")String componentFile,
                                          @RequestParam(required = false) @ApiParam(value = "组件logo地址")MultipartFile logo,
                                          HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        List<ComponentParam> params=null;
        if(param!=null){
            JSONArray jsonArray= JSONArray.parseArray(param);
            params=jsonArray.toJavaList(ComponentParam.class);
        }
        boolean isSuccess = componentService.createComponent(name,type,desc,image,params,userId,componentFile,logo);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "成功创建系统组件", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "创建系统组件失败", false);
        }
    }

    @ResponseBody
    @ApiOperation("删除组件")
    @RequestMapping(value = "/component/deleteComponentById", method = RequestMethod.POST)
    public ResponseResult deleteComponentById(@RequestParam @ApiParam(value = "组件Id")Long componentId,
                                              HttpSession httpSession){
        Long userId = Long.valueOf(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = componentService.deleteComponentById(componentId);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "成功删除组件", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "删除组件失败", false);
        }
    }

    @ApiOperation("添加系统的工作负载")
    @PostMapping("/addSystemWorkload")
    public ResponseResult addSystemWorkload(
            HttpSession session,
            @ApiParam(name = "workloadName", value = "系统工作负载名", type = "String") @RequestParam String workloadName,
            @ApiParam(name = "workloadVersion", value = "系统工作负载版本",  type = "String") @RequestParam String workloadVersion,
            @ApiParam(name = "performance", value = "系统工作负载性能文件") @RequestParam MultipartFile performance,
            @ApiParam(name = "workloadTestCmd", value = "系统工作负载可用性测试shell命令串",  type = "String") @RequestParam(required = false) String workloadTestCmd,
            @RequestParam(required = false) @ApiParam(value = "待调优系统logo")MultipartFile logo,
            @ApiParam(name = "execParams", value = "执行参数", type="String") @RequestParam String execParams
    ) {
        Long userId = (Long) session.getAttribute("user_id");
        if(logo==null){
            System.out.println("fhrgqygfqriyy");
        }
        SystemWorkload systemWorkload = workloadService.addSystemWorkload(userId, workloadName,workloadVersion, performance, workloadTestCmd,execParams,logo);
        if (systemWorkload == null) {
            return new ResponseResult(HttpCode.SERVER_ERROR,"系统工作负载添加失败，请检查配置后重试");
        }
        return new ResponseResult(HttpCode.OK,systemWorkload);
    }

    @ApiOperation("删除系统的工作负载")
    @PostMapping("/deleteSystemWorkload")
    public ResponseResult deleteSystemWorkload(
            HttpSession session,
            @ApiParam(name = "systemWorkloadId", value = "系统工作负载id", type = "Long") @RequestParam Long systemWorkloadId
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        try {
            workloadService.deleteSystemWorkload(userId, systemWorkloadId);
        }catch (Exception e){
            log.error("用户userId:{}删除系统工作负载workloadId:{}失败", userId, systemWorkloadId);
            e.printStackTrace();
            return new ResponseResult(HttpCode.SERVER_ERROR,"环境系统工作负载失败");
        }
        return new ResponseResult(HttpCode.OK,null);
    }

    @ApiOperation("修改系统的工作负载")
    @PostMapping("/updateSystemWorkload")
    public ResponseResult updateSystemWorkload(
            HttpSession session,
            @ApiParam(name = "systemWorkloadId", value = "系统工作负载id", type = "Long") @RequestParam Long systemWorkloadId,
            @ApiParam(name = "workloadName", value = "系统工作负载名", type = "String") @RequestParam(required = false) String workloadName,
            @ApiParam(name = "workloadVersion", value = "系统工作负载版本",  type = "String") @RequestParam(required = false) String workloadVersion,
            @ApiParam(name = "performance", value = "系统工作负载版本",  type = "String") @RequestParam(required = false) MultipartFile performance,
            @ApiParam(name = "workloadTestCmd", value = "系统工作负载可用性测试shell命令串",  type = "String") @RequestParam(required = false) String workloadTestCmd,
            @ApiParam(value = "工作负载Logo")@RequestParam(required = false) MultipartFile logo,
            @ApiParam(name = "execParams", value = "执行参数", type = "String") @RequestParam(required = false) String execParams
    ) {
        Long userId = (Long) session.getAttribute("user_id");

        SystemWorkload systemWorkload = null;
        try {
            systemWorkload = workloadService.updateSystemWorkload(userId, systemWorkloadId, workloadName, workloadVersion, performance, workloadTestCmd,execParams,logo);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(HttpCode.SERVER_ERROR,"系统工作负载修改失败，请检查配置后重试");
        }
        return new ResponseResult(HttpCode.OK,systemWorkload);
    }

}
