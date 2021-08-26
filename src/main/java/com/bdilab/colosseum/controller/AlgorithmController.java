package com.bdilab.colosseum.controller;

import com.bdilab.colosseum.domain.Algorithm;
import com.bdilab.colosseum.domain.Experiment;
import com.bdilab.colosseum.response.HttpCode;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.service.AlgorithmService;
import com.bdilab.colosseum.service.ExperimentService;
import com.bdilab.colosseum.service.PipelineService;
import com.bdilab.colosseum.vo.ExperimentVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author duyeye
 * @Date 2021/1/6 0006 12:11
 */
@Controller
public class AlgorithmController {

    @Autowired
    AlgorithmService algorithmService;

    @Autowired
    PipelineService pipelineService;

    @Autowired
    ExperimentService experimentService;

    @ResponseBody
    @ApiOperation("创建算法")
    @RequestMapping(value = "/algorithm/createAlgorithm", method = RequestMethod.POST)
    public ResponseResult createAlgorithm(@RequestParam @ApiParam(value = "算法名称") String algorithmName,
                                          @RequestParam @ApiParam(value = "算法描述")String algorithmDesc,
                                          @RequestParam @ApiParam(value = "算法标签")String algorithmTag,
                                          @RequestParam @ApiParam(value = "算法json")String algorithmJson,
                                          @RequestParam(required = false) @ApiParam(value = "算法logo")MultipartFile logo,
                                          @RequestParam(required = false) @ApiParam(value = "算法的模板标记",defaultValue = "0")Integer isTemplate,
                                          HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("user_id").toString());
        boolean isSuccess = algorithmService.createAlgorithm(algorithmName,algorithmDesc,algorithmTag,algorithmJson,logo,isTemplate,userId);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "Algorithm created successfully", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "Failed to create algorithm", true);
        }
    }

    @ResponseBody
    @ApiOperation("分页获取该用户的所有算法")
    @RequestMapping(value = "/algorithm/getAlgorithmByUserId", method = RequestMethod.POST)
    public ResponseResult getAlgorithmByUserId(@RequestParam @ApiParam(value = "页数")int pageNum,
                                               @RequestParam @ApiParam(value = "每页记录数")int pageSize,
                                               HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("user_id").toString());
        long role = Long.parseLong(httpSession.getAttribute("role").toString());
        Map<String, Object> data=new HashMap<>();
        if(role==1){//管理员
            data = algorithmService.getAlgorithmByUserId(pageNum, pageSize, userId);
        }else{//普通用户
            data = algorithmService.getAllAlgorithmByUserId(pageNum, pageSize, userId);
        }
        return new ResponseResult(HttpCode.OK.getCode(), "Successfully obtained all algorithms", true, data);
    }

    @ResponseBody
    @ApiOperation("分页获取该用户的所有算法（普通用户可以获取模板算法）")
    @RequestMapping(value = "/algorithm/getALLAlgorithmByUserId", method = RequestMethod.POST)
    public ResponseResult getALLAlgorithmByUserId(@RequestParam @ApiParam(value = "页数")int pageNum,
                                                  @RequestParam @ApiParam(value = "每页记录数")int pageSize,
                                                  HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("user_id").toString());
        long role = Long.parseLong(httpSession.getAttribute("role").toString());
        Map<String, Object> data=new HashMap<>();
        if(role==1){//管理员
            data = algorithmService.getAlgorithmByUserId(pageNum, pageSize, userId);
        }else{//普通用户
            data = algorithmService.getAllAlgorithmByUserId(pageNum, pageSize, userId);
        }
        return new ResponseResult(HttpCode.OK.getCode(), "Successfully obtained all algorithms", true, data);
    }

    @ResponseBody
    @ApiOperation("根据id删除算法")
    @RequestMapping(value = "/algorithm/deleteAlgorithmById", method = RequestMethod.POST)
    public ResponseResult deleteAlgorithmById(@RequestParam @ApiParam(value = "算法id")Long algorithmId,
                                              HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("user_id").toString());
        long role = Long.parseLong(httpSession.getAttribute("role").toString());
        //Ordinary users do not have permission to delete the template algorithm provided by the administrator
        Algorithm algorithm=algorithmService.selectAlgorithmById(algorithmId);
        if(algorithm.getIsTemplate()==1&&role!=1){
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "You do not have permission to delete this algorithm", false);
        }
        //If the algorithm has been used in experiments, it is not allowed to delete it
        List<Experiment> experimentList=experimentService.getExperimentByuserId(userId);
        for(Experiment experiment:experimentList){
            if(experiment.getFkAlgorithmIds().contains(algorithm.getId().toString())){
                return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "The algorithm has been used in experiments. Please delete the experiment first", false);
            }
        }
        boolean isSuccess = algorithmService.deleteAlgorithmById(algorithmId);
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "Algorithm deleted successfully", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "Failed to delete algorithm", false);
        }
    }

    @ResponseBody
    @ApiOperation("修改算法")
    @RequestMapping(value = "/algorithm/updateAlgorithmById", method = RequestMethod.POST)
    public ResponseResult updateAlgorithmById(@RequestParam @ApiParam(value = "算法id")Long algorithmId,
                                              @RequestParam(required = false) @ApiParam(value = "算法名称")String algorithmName,
                                              @RequestParam(required = false) @ApiParam(value = "算法描述")String algorithmDesc,
                                              @RequestParam(required = false) @ApiParam(value = "算法标签")String algorithmTag,
                                              @RequestParam(required = false) @ApiParam(value = "算法json")String algorithmJson,
                                              @RequestParam(required = false) @ApiParam(value = "算法logo")MultipartFile logo,
                                              @RequestParam @ApiParam(value="算法模板标记")Integer isTemplate,
                                              HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("user_id").toString());
        long role = Long.parseLong(httpSession.getAttribute("role").toString());
        boolean isSuccess=false;
        Algorithm algorithm=algorithmService.selectAlgorithmById(algorithmId);
        if(algorithm.getIsTemplate()==1&&role!=1){
            isSuccess = algorithmService.createAlgorithm(algorithmName,algorithmDesc,algorithmTag,algorithmJson,logo,isTemplate,userId);
        }else{
            isSuccess = algorithmService.updateAlgorithmById(algorithmId,algorithmName,algorithmDesc,algorithmTag,algorithmJson,logo,isTemplate);
        }
        if (isSuccess){
            return new ResponseResult(HttpCode.OK.getCode(), "Algorithm modified successfully", true);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "Failed to modify algorithm", false);
        }
    }

    @Deprecated
    @ResponseBody
    @ApiOperation("获取算法的输出结果")
    @RequestMapping(value = "/algorithm/getArtifacts", method = RequestMethod.POST)
    public ResponseResult getArtifacts(@RequestParam @ApiParam(value = "runId")Long runId,
                                              @RequestParam @ApiParam(value = "nodeId")String nodeId,
                                              @RequestParam @ApiParam(value = "artifactName")String artifactName,
                                              HttpSession httpSession){
        Long userId = Long.parseLong(httpSession.getAttribute("uesr_id").toString());
        String result = pipelineService.getArtifactData(runId,nodeId,artifactName);
        if (!result.equals("")){
            return new ResponseResult(HttpCode.OK.getCode(), "成功修改算法", true, result);
        }else {
            return new ResponseResult(HttpCode.SERVER_ERROR.getCode(), "修改算法失败", false);
        }
    }
}
