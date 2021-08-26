package com.bdilab.colosseum.domain;

import lombok.Builder;

import java.util.Date;

@Builder
public class Experiment {
    private Long id;

    private String experimentName;

    private String description;

    private Long fkUserId;

    private Long fkSysEnvId;

    private String allParams;

    private String blackList;

    private String whiteList;

    // 修改标记++++++++++++
    private String fkAlgorithmIds;

    // 这个参数本来就是属于算法的，现在的实验要支持多算法，该字段已经没用了
    @Deprecated
    private String ggeditorObjectString;

    private String performance;

    private String metricsSetting;

    private String resultSetting;

    private Date createTime;

    private Date modifyTime;

    private String logoPath;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName == null ? null : experimentName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public Long getFkSysEnvId() {
        return fkSysEnvId;
    }

    public void setFkSysEnvId(Long fkSysEnvId) {
        this.fkSysEnvId = fkSysEnvId;
    }

    public String getAllParams() {
        return allParams;
    }

    public void setAllParams(String allParams) {
        this.allParams = allParams;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public String getFkAlgorithmIds() {
        return fkAlgorithmIds;
    }

    public void setFkAlgorithmIds(String fkAlgorithmIds) {
        this.fkAlgorithmIds = fkAlgorithmIds;
    }

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getMetricsSetting() {
        return metricsSetting;
    }

    public void setMetricsSetting(String metricsSetting) {
        this.metricsSetting = metricsSetting == null ? null : metricsSetting.trim();
    }

    public String getResultSetting() {
        return resultSetting;
    }

    public void setResultSetting(String resultSetting) {
        this.resultSetting = resultSetting == null ? null : resultSetting.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}