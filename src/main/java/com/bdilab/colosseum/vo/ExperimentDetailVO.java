package com.bdilab.colosseum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ExperimentDetailVO 实验详情VO
 * @Author wx
 * @Date 2020/12/31 0031 17:34
 **/
@Builder
@Data
// 构造注解要加，否则数据库查询结果无法映射赋值给对象属性
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentDetailVO {
    // 实验ID
    private Long id;
    // 实验名
    private String experimentName;
    // 实验描述
    private String description;
    // 用户ID
    private Long userId;
    // 创建者名
    private String creator;
    // 环境配置ID
    private Long sysEnvId;
    // 环境配置内容
    private SystemEnvVO systemEnv;

    // 参数列表（黑白名单）
    private String allParams;

    private String blackList;

    private String whiteList;
    // 算法信息
    // 算法ID
    // 修改标记++++++++++++++
    private String fkAlgorithmIds;

//    private String algorithmName;
//
//    private String tag;
//
//    private String algorithmDescription;
//
//    // 实验配置当时的算法json
//    private String ggeditorObjectString;
    private List<AlgorithmForExpDetailVO> algorithmVOS;

    // 性能指标
    private String performance;

    // 度量指标
    private String metricsSetting;
    // 结果呈现类型
    private String resultSetting;

    // 创建时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    // 更新时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;
}
