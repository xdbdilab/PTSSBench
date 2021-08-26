package com.bdilab.colosseum.bo;

import lombok.Data;

/**
 * @ClassName ExperimentBO 运行实验BO，主要是为了根据实验ID获取调用实验运行createRun接口需要的参数
 * @Author wx
 * @Date 2021/1/15 0015 11:11
 **/
@Data
@Deprecated
public class ExperimentBO {

    private Long id;

    private Long fkAlgorithmId;

    // 参数列表
    private String allParams;

    // 黑名单
    private String blackList;

    // 白名单
    private String whiteList;

    private String performance;

    private String metricsSetting;

    private Long fkSysEnvId;

    private String pipelineId;

    // 每个结点/组件的参数值
    private String paramValue;
}
