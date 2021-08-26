package com.bdilab.colosseum.domain;

import java.util.Date;

public class Algorithm {
    private Long id;

    private String algorithmName;

    private String tag;

    private String description;

    private Long fkUserId;

    private String pipelineAddr;

    private String pipelineYaml;

    private String pipelineId;

    private String paramValue;

    private Date createTime;

    private Date modifyTime;

    private String ggeditorObjectString;

    private String logoPath;

    private Integer isTemplate;

    public Integer getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Integer isTemplate) {
        this.isTemplate = isTemplate;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName == null ? null : algorithmName.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
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

    public String getPipelineAddr() {
        return pipelineAddr;
    }

    public void setPipelineAddr(String pipelineAddr) {
        this.pipelineAddr = pipelineAddr == null ? null : pipelineAddr.trim();
    }

    public String getPipelineYaml() {
        return pipelineYaml;
    }

    public void setPipelineYaml(String pipelineYaml) {
        this.pipelineYaml = pipelineYaml == null ? null : pipelineYaml.trim();
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId == null ? null : pipelineId.trim();
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue == null ? null : paramValue.trim();
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

    public String getGgeditorObjectString() {
        return ggeditorObjectString;
    }

    public void setGgeditorObjectString(String ggeditorObjectString) {
        this.ggeditorObjectString = ggeditorObjectString == null ? null : ggeditorObjectString.trim();
    }
}