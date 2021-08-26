package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.bo.ExperimentBO;
import com.bdilab.colosseum.bo.ExperimentBO_V2;
import com.bdilab.colosseum.domain.Experiment;
import com.bdilab.colosseum.vo.ExperimentDetailVO;
import com.bdilab.colosseum.vo.ExperimentVO;

import java.util.List;

public interface ExperimentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Experiment record);

    int insertSelective(Experiment record);

    Experiment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Experiment record);

    int updateByPrimaryKey(Experiment record);

    /**
     * 根据userId查询ExperimentVO实验列表对象
     * @param userId
     * @return
     */
    List<ExperimentVO> selectByUserId(Long userId);

    /**
     * 根据experimentId查看实验表中是否有数据存在
     * @param experimentId
     * @return
     */
    int checkById(Long experimentId);

    /**
     * 根据experimentId查询某个实验详情
     * @param experimentId
     * @return
     */
    ExperimentDetailVO selectByExperimentId(Long experimentId);

    /**
     * 根据experimentId查询运行实验需要的数据（pipeline_id param_value performance metrics_setting)
     * @param experimentId
     * @return
     */
    @Deprecated
    ExperimentBO selectExperimentBOByExperimentId(Long experimentId);

    // 修改标记+++++++++++++++++++++++
    /**
     * 根据experimentId查询运行实验需要的数据
     * @param experimentId
     * @return
     */
    ExperimentBO_V2 selectExperimentBOByExperimentId_V2(Long experimentId);

    /**
     * 根据用户id获取experiemnt表中的相关信息
     * @param userId
     * @return
     */
    List<Experiment> selectAllExperimentsByUserId(Long userId);

}