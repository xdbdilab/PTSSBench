package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.Algorithm;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author duyeye
 * @Date 2021/1/6 0006 13:17
 */
public interface AlgorithmService {
    /**
     * 创建算法
     * @param algorithmName
     * @param algorithmDesc
     * @param algorithmTag
     * @param algorithmJson
     * @param userId
     * @return
     */
    boolean createAlgorithm(String algorithmName, String algorithmDesc, String algorithmTag, String algorithmJson, MultipartFile logo, Integer isTemplate, Long userId);

    /**
     * 分页获取该用户的所有算法
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    Map<String, Object> getAlgorithmByUserId(int pageNum, int pageSize, Long userId);

    /**
     * 分页获取普通用户的算法(包括自定义的算法以及管理员提供的模板算法)
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    Map<String, Object> getAllAlgorithmByUserId(int pageNum, int pageSize, Long userId);

    /**
     * 根据id删除算法
     * @param algorithmId
     * @return
     */
    boolean deleteAlgorithmById(Long algorithmId);

    /**
     * 修改算法的名称和描述
     * @param algorithmId
     * @param algorithmName
     * @param algorithmDesc
     * @param algorithmTag
     * @param algorithmJson
     * @param algorithmLogo
     * @return
     */
    boolean updateAlgorithmById(Long algorithmId, String algorithmName, String algorithmDesc, String algorithmTag, String algorithmJson,MultipartFile algorithmLogo,Integer isTemplate);

    /**
     * 通过算法id获取算法信息
     * @param algorithmId
     * @return
     */
    Algorithm selectAlgorithmById(Long algorithmId);
}
