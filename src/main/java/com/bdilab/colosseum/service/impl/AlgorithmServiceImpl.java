package com.bdilab.colosseum.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.domain.Algorithm;
import com.bdilab.colosseum.domain.workflow.ComponentParamsInOrder;
import com.bdilab.colosseum.exception.BusinessException;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.mapper.AlgorithmMapper;
import com.bdilab.colosseum.service.AlgorithmService;
import com.bdilab.colosseum.service.PipelineService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.vo.AlgorithmVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @Author duyeye
 * @Date 2021/1/6 0006 13:17
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    @Resource
    AlgorithmMapper algorithmMapper;

    @Autowired
    PipelineService pipelineService;

    @Value("${logo.path}")
    private String logoPath;

    @Value("${image.map}")
    private String imageMap;

    @Override
    public boolean createAlgorithm(String algorithmName, String algorithmDesc, String algorithmTag, String algorithmJson, MultipartFile logo,Integer isTemplate, Long userId) {
        //检验算法各组件连接是否正确
        String componentNamesJoinInString = pipelineService.getComponentNamesJoinInString(algorithmJson);
        String buildInAlgorithmName = Constant.BUILD_IN_ALGORITHM.getOrDefault(componentNamesJoinInString,null);
        if(buildInAlgorithmName == null){
            throw new BusinessException(400,"invalid link order","用户的组件连接顺序不属于任何一个算法");
        }

        boolean uploadSuccess=true;
        //如果Logo为null，交个前端处理（前端使用默认Logo）
        String path="";
        if(logo!=null){
            String logoPath1=this.logoPath+userId;
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="algorithm_"+UUID.randomUUID() +"."+strs[strs.length-1];
            uploadSuccess=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        if(!uploadSuccess) {
            return false;
        }

        Algorithm algorithm = new Algorithm();
        Map map = pipelineService.generatePipeline(algorithmJson);
        String pipelineName = algorithmName + "_" + UUID.randomUUID();
        String pipelineId = pipelineService.uploadPipeline(pipelineName,algorithmDesc,new File(map.get("yamlFilePath").toString()));

        algorithm.setAlgorithmName(algorithmName);
        algorithm.setTag(algorithmTag);
        algorithm.setDescription(algorithmDesc);

        algorithm.setFkUserId(userId);
        algorithm.setGgeditorObjectString(algorithmJson);
        algorithm.setPipelineAddr(map.get("pyFilePath").toString());
        algorithm.setPipelineYaml(map.get("yamlFilePath").toString());
        algorithm.setLogoPath(path);
        algorithm.setIsTemplate(isTemplate);

        algorithm.setPipelineId(pipelineId);
        ComponentParamsInOrder componentParamsInOrder = (ComponentParamsInOrder)map.get("componentParamsInOrder");
        String jsonString = JSONObject.toJSONString(componentParamsInOrder.getComponentParamsInOrder());
        algorithm.setParamValue(jsonString);
        algorithm.setCreateTime(new Date());
        int insert = algorithmMapper.insert(algorithm);
        return insert == 1;

//        Algorithm algorithm = new Algorithm();
//        Map map = pipelineService.generatePipeline(algorithmJson);
//        String pipelineName = algorithmName + "_" + UUID.randomUUID();
//        String pipelineId = pipelineService.uploadPipeline(pipelineName,algorithmDesc,new File(map.get("yamlFilePath").toString()));
//        //Map<String, Object> paramValue = JsonUtils.getParamsValueFromWorkflowJson(algorithmJson);

//        algorithm.setAlgorithmName(algorithmName);
//        algorithm.setTag(algorithmTag);
//        algorithm.setDescription(algorithmDesc);
//        algorithm.setFkUserId(userId);
//        algorithm.setGgeditorObjectString(algorithmJson);
//        algorithm.setPipelineAddr(map.get("pyFilePath").toString());
//        algorithm.setPipelineYaml(map.get("yamlFilePath").toString());
//        algorithm.setPipelineId(pipelineId);
//        ComponentParamsInOrder componentParamsInOrder = (ComponentParamsInOrder)map.get("componentParamsInOrder");
//        String jsonString = JSONObject.toJSONString(componentParamsInOrder.getComponentParamsInOrder());
//        algorithm.setParamValue(jsonString);
//        algorithm.setCreateTime(new Date());
//        algorithmMapper.insert(algorithm);
//        return true;
    }


    @Override
    public Map<String, Object> getAlgorithmByUserId(int pageNum, int pageSize, Long userId) {
        Map<String, Object> data = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Algorithm> algorithms = algorithmMapper.selectAlgorithmByUserId(userId);
        List<AlgorithmVO> algorithmVOS = new LinkedList<>();
        for (Algorithm algorithm:algorithms){
            AlgorithmVO algorithmVO;
            try{
                algorithmVO = buildAlgorithmVO(algorithm);
                if(!(algorithm.getLogoPath().equals(""))){
                    algorithmVO.setLogo(imageMap+algorithmVO.getLogo());
                }
                algorithmVOS.add(algorithmVO);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        PageInfo pageInfo = new PageInfo<>(algorithmVOS);
        data.put("algorithm list",algorithmVOS);
        data.put("pageNum",pageInfo.getPageNum());
        data.put("total",pageInfo.getTotal());
        return data;
    }
    @Override
    //获取模板算法和自定义算法
    public Map<String, Object> getAllAlgorithmByUserId(int pageNum, int pageSize, Long userId) {
        Map<String, Object> data = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Algorithm> algorithms = algorithmMapper.selectAlgorithmByUserId(userId);
        //获取管理员模板算法
        List<Algorithm> algorithms1 = algorithmMapper.selectTemplateAlgorithm();
        algorithms.addAll(algorithms1);
        List<AlgorithmVO> algorithmVOS = new LinkedList<>();
        for (Algorithm algorithm:algorithms){
            AlgorithmVO algorithmVO;
            try{
                algorithmVO = buildAlgorithmVO(algorithm);
                if(!(algorithm.getLogoPath().equals(""))){
                    algorithmVO.setLogo(imageMap+algorithmVO.getLogo());
                }
                algorithmVOS.add(algorithmVO);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        PageInfo pageInfo = new PageInfo<>(algorithmVOS);
        data.put("algorithm list",algorithmVOS);
        data.put("pageNum",pageInfo.getPageNum());
        data.put("total",pageInfo.getTotal());
        return data;
    }

    @Override
    public boolean deleteAlgorithmById(Long algorithmId) {
        String pipelineId = algorithmMapper.selectByPrimaryKey(algorithmId).getPipelineId();
        if (!pipelineId.equals("")){
            //删除算法对应的pipeline
            pipelineService.deletePipelineById(pipelineId);
        }
        //删除数据库中对应的logo图
        Algorithm algorithm=algorithmMapper.selectByPrimaryKey(algorithmId);
        String logoPath=algorithm.getLogoPath();
        if(!logoPath.equals("")){
            File file=new File(logoPath);
            file.delete();
        }
        //删除数据库中的本条记录
        algorithmMapper.deleteByPrimaryKey(algorithmId);
        return true;
    }

    @Override
    public boolean updateAlgorithmById(Long algorithmId, String algorithmName, String algorithmDesc,
                                       String algorithmTag, String algorithmJson,MultipartFile algorithmLogo,Integer isTemplate) {
        //检验算法各组件连接是否正确
        String componentNamesJoinInString = pipelineService.getComponentNamesJoinInString(algorithmJson);
        String buildInAlgorithmName = Constant.BUILD_IN_ALGORITHM.getOrDefault(componentNamesJoinInString,null);
        if(buildInAlgorithmName == null){
            throw new BusinessException(400,"invalid link order","用户的组件连接顺序不属于任何一个算法");
        }

        Algorithm algorithm = new Algorithm();
        algorithm.setId(algorithmId);
        algorithm.setAlgorithmName(algorithmName);
        algorithm.setDescription(algorithmDesc);
        algorithm.setTag(algorithmTag);
        algorithm.setGgeditorObjectString(algorithmJson);
        algorithm.setIsTemplate(isTemplate);
        algorithm.setModifyTime(new Date());
        boolean uploadSuccess=true;
        String path=null;
        if(algorithmLogo!=null){
            //删除之前的logo图片
            Algorithm algorithm1=algorithmMapper.selectByPrimaryKey(algorithmId);
            if (!algorithm1.getLogoPath().equals("")){
                File file=new File(algorithm1.getLogoPath());
                file.delete();
            }

            Long userId=algorithm1.getFkUserId();
            String logoPath1=this.logoPath+userId;
            String[] strs=algorithmLogo.getOriginalFilename().split("\\.");
            String fileName="algorithm_"+UUID.randomUUID() +"."+strs[strs.length-1];
            uploadSuccess=FileUtils.uploadFileRename(algorithmLogo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
            algorithm.setLogoPath(path);
        }
        if(uploadSuccess){
            algorithmMapper.updateByPrimaryKeySelective(algorithm);
        }else{
            return false;
        }
        return true;
    }

    private AlgorithmVO buildAlgorithmVO(Algorithm algorithm)throws Exception{
        AlgorithmVO algorithmVO = new AlgorithmVO();
        algorithmVO.setId(algorithm.getId());
        algorithmVO.setAlgorithmName(algorithm.getAlgorithmName());
        algorithmVO.setTag(algorithm.getTag());
        algorithmVO.setDescription(algorithm.getDescription());
        algorithmVO.setGgeditorObjectString(algorithm.getGgeditorObjectString());
        algorithmVO.setParamValue(algorithm.getParamValue());
        algorithmVO.setCreateTime(algorithm.getCreateTime());
        algorithmVO.setModifyTime(algorithm.getModifyTime());
        algorithmVO.setLogo(algorithm.getLogoPath());
        algorithmVO.setIsTemplate(algorithm.getIsTemplate());
        return algorithmVO;
    }
    @Override
    public Algorithm selectAlgorithmById(Long algorithmId) {
        Algorithm algorithm=algorithmMapper.selectByPrimaryKey(algorithmId);
        return algorithm;
    }
}
