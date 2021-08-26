package com.bdilab.colosseum.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Author duyeye
 * @Date 2020/12/22 0022 11:24
 */
@Service
public interface PipelineService {

    /**
     * 上传pipeine
     * @param name
     * @param description
     * @param file
     * @return pipelineId
     */
    String uploadPipeline(String name, String description, File file);

    /**
     * 根据pipelineId获取pipeline
     * @param pipelineId
     * @return
     */
    String getPipelineById(String pipelineId);

    /**
     * 根据pipelineId删除pipeline
     * @param pipelineId
     * @return
     */
    boolean deletePipelineById(String pipelineId);

    /**
     * 携带参数值，创建运行
     * @param pipelineId
     * @param pipelineName
     * @param parameter
     * @return runId
     */
    String createRun(String pipelineId, String pipelineName, ArrayList<ArrayList<String>> parameter);

    /**
     * 根据runId查看运行状态
     * @param runId
     * @return
     */
    boolean checkFailedStatus(String runId);

    /**
     * 根据runId删除run
     * @param runId
     * @return
     */
    boolean deleteRunById(String runId);

    /**
     * 根据runId暂停run
     * @param runId
     * @return
     */
    boolean terminateRunById(String runId);

    /**
     * 重新运行一个失败的或暂停的run
     * @param runId
     * @return
     */
    boolean retryRunById(String runId);

    /**
     * 获得组件输出数据
     * @param runId
     * @param nodeId
     * @param artifactName
     * @return
     */
    @Deprecated
    String getArtifactData(Long runId, String nodeId, String artifactName);

    /**
     * 根据json生成pipeline的py和yaml文件
     * @param json
     * @return
     */
    Map generatePipeline(String json);

    /**
     * 检验算法的各组件连接顺序是否正确
     * @param algorithmJson 算法json串
     * @return 是否正确
     */
    String getComponentNamesJoinInString(String algorithmJson);
}
