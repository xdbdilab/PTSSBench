package com.bdilab.colosseum.mapper;

import com.bdilab.colosseum.domain.ExperimentRun;
import com.bdilab.colosseum.vo.ExperimentResultDetailVO;
import com.bdilab.colosseum.vo.ExperimentResultVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExperimentRunMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExperimentRun record);

    int insertSelective(ExperimentRun record);

    ExperimentRun selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExperimentRun record);

    int updateByPrimaryKey(ExperimentRun record);

    int updateResultByRunId(ExperimentRun record);

    /**
     * 根据id查询实验结果表中是否有数据
     * @param resultId
     * @return
     */
    int checkById(Long resultId);

    /**
     * 根据experimentId和userId查询该用户实验结果列表对象
     * @return
     */
    List<ExperimentResultVO> selectByExperimentIdAndUserId(@Param("experimentId") Long experimentId);

    /**
     * 根据id查询实验结果详情
     * @param resultId
     * @return
     */
    ExperimentResultDetailVO selectByResultId(Long resultId);

    /**
     * 根据实验ID获取实验运行runid集合
     * @param experimentId
     * @return
     */
    List<String> selectRunIdByExperimentId(Long experimentId);

    /**
     * 根据实验ID获取实验运行结果（获取status为运行中的值）
     * @param experimentId
     * @return
     */
    List<ExperimentRun> selectByExperimentId(Long experimentId);

    /**
     * 根据实验ID删除实验运行结果
     * @param experimentId
     * @return
     */
    int deleteByExperimentId(Long experimentId);

    /**
     * 根据实验ID清空runId
     * @param experimentId
     * @return
     */
    int deleteRunIdByExperimentId(Long experimentId);

    /**
     * 根据实验ID判断是否有实验在运行中
     * @param experimentId
     * @return
     */
    int checkRunningExperimentByExperimentId(Long experimentId);
}