package com.bdilab.colosseum.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ExperimentBO 运行实验BO，主要是为了根据实验ID获取调用实验运行createRun接口需要的参数
 * @Author liran
 * @Date 2021/6/21 0015 11:11
 **/
@Data
public class ExperimentBO_V2 {

    private Long id;

    private String fkAlgorithmIds;

    // 参数列表
    private String allParams;

    // 黑名单
    private String blackList;

    // 白名单
    private String whiteList;

    private String performance;

    private String metricsSetting;

    private Long fkSysEnvId;
}
