package com.bdilab.colosseum.service;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.domain.Experiment;
import com.bdilab.colosseum.domain.ExperimentRun;
import com.bdilab.colosseum.enums.ExperimentRunningStatus;
import com.bdilab.colosseum.vo.ExperimentDetailVO;
import com.bdilab.colosseum.vo.ExperimentResultDetailVO;
import com.bdilab.colosseum.vo.ExperimentResultVO;
import com.bdilab.colosseum.vo.ExperimentVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Name ExperimentService
 * @Author wx
 * @Date 2020/12/31 0031 16:44
 **/
public interface ExperimentService {

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
    int add(String experimentName, String description, Long userId, Long sysEnvId,
            String allParams, String blackList, String whiteList,
            String algorithmIds, String performance,
            String metricsSetting, String resultSetting, MultipartFile logo);

    /**
     * 检测添加的算法是否合法
     * @param algorithmIds
     * @return
     */
    boolean checkAlgorithms(List<Long> algorithmIds,Long userId);

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
    int update(Long experimentId, String description, Long sysEnvId,
               String allParams, String blackList, String whiteList,
               String algorithmIds,
               String performance, String metricsSetting, String resultSetting,MultipartFile logo);

    /**
     * 展示用户创建的实验列表
     * @param userId
     * @return
     */
    List<ExperimentVO> getList(Long userId);

    /**
     * 根据experimentId检查实验是否存在
     * @param experimentId
     * @return
     */
    boolean checkByExperimentId(Long experimentId);

    /**
     * 根据experimentId检查是否有相同实验在运行中（实验环境是否有在被占用）
     * @param experimentId
     * @return
     */
    boolean checkRunningExperimentByExperimentId(Long experimentId);

    /**
     * 根据experimentId查询返回实验详情
     * @param experimentId
     * @return
     */
    ExperimentDetailVO getByExperimentId(Long experimentId);

    /**
     * 运行实验（对于暂不确定返回值的先使用void）
     * @param experimentId
     */
     Map<String, Object> run(Long experimentId, Map<String, String> execParams, Integer restrictNumber);

    /**
     * python端调用，根据experimentRunId修改数据表中experiment_run的状态，根据conversationId+sse会话传递消息给前端，根据resultFilePath读取执行结果
     * @param experimentRunId
     * @param conversationId
     * @param resultFilePath
     * @return
     */
     boolean pushData(Long experimentRunId, String conversationId, String componentName, String resultFilePath);

    /**
     * 查看历史实验结果列表
     * @return
     */
    List<JSONObject> getResultList(Long experimentId);

    /**
     * 根据resultId检查实验结果是否存在
     * @param resultId
     * @return
     */
    boolean checkByResultId(Long resultId);

    /**
     * 根据resultId返回实验结果详情
     * @param resultId
     * @return
     */
    @Deprecated
    ExperimentResultDetailVO getResultDetail(Long resultId);

//    /**
//     * 根据resultId终止实验
//     * @param resultId
//     */
//     boolean terminate(Long resultId);

    /**
     * 根据算法端传来的paramList,performance,再根据找到对应的workload进行调优实验
     * @param workload
     * @param performance
     * @param paramList
     * @return
     */
    String getPerformance(Workload workload, String paramList, String performance);

    /**
     * 根据实验ID删除实验
     * @param experimentId
     * @return
     */
    boolean delete(Long experimentId);

    /**
     * 根据runID来更新实验运行结果
     * @param conversationId
     * @param data
     * @return
     */

    int saveResult(String conversationId,String data);
    /**
     * 根据实验ID获取实验运行的最新runId
     *
     * @param experimentId
     * @return
     */

    String getNewestRunIdbyExperimentId(Long experimentId);

    /**
     * 每次算法运行完毕，更新experiment_run表的run_id
     */
    void updateRunIdINExperimentRun(Long experimentRunId,String runId);

    /**
     * 更新experiment_run表的运行状态
     */
    void updateStatusINExperimentRun(Long experimentRunId, ExperimentRunningStatus status, boolean isEnd);

    /**
     * 通过experimentRunId查看运行记录
     * @param experimentRunId
     * @return
     */
    ExperimentRun getExperimentByRunId(Long experimentRunId);

    /**
     * 获取该用户的所有实验信息
     * @param userId
     * @return
     */
    List<Experiment> getExperimentByuserId(Long userId);
}
