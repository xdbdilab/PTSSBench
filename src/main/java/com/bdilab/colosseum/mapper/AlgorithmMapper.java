package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.Algorithm;
import com.bdilab.colosseum.vo.AlgorithmForExpDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlgorithmMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Algorithm record);

    int insertSelective(Algorithm record);

    Algorithm selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Algorithm record);

    int updateByPrimaryKeyWithBLOBs(Algorithm record);

    int updateByPrimaryKey(Algorithm record);

    /**
     * 根据userId获取所有算法
     * @param userId
     * @return
     */
    List<Algorithm> selectAlgorithmByUserId(Long userId);

    // 修改标记++++++++++++
    /**
     * 根据一个算法Id列表，得到其有效性的个数
     * @param algorithmIds
     * @param userId
     */
    int countValidAlgorithmNums(@Param("algorithmIds") List<Long> algorithmIds, @Param("userId")Long userId);

    // 修改标记++++++++++++
    /**
     * 为了展示实验详情而查询相关算法
     * @param algorithmIds
     */
    List<AlgorithmForExpDetailVO> selectAgForEpDetailVOByAgIds(@Param("algorithmIds") List<Long> algorithmIds);

    /**
     * 获取所有管理员创建的模板算法
     * @return
     */
    List<Algorithm> selectTemplateAlgorithm();
}