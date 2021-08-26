package com.bdilab.colosseum.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

public class SystemEnv {
    private Long id;

    private String envName;

    private String envDesc;

    private Long fkSutId;

    private String parameters;

    private Long fkHardwareId;

    private Long fkUserId;

    private Long fkWorkloadId;

    private String performance;

    private Date createTime;

    private Date modifyTime;

    private String logoPath;

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

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public Long getFkSutId() {
        return fkSutId;
    }

    public void setFkSutId(Long fkSutId) {
        this.fkSutId = fkSutId;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters == null ? null : parameters.trim();
    }

    public Long getFkHardwareId() {
        return fkHardwareId;
    }

    public void setFkHardwareId(Long fkHardwareId) {
        this.fkHardwareId = fkHardwareId;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public Long getFkWorkloadId() {
        return fkWorkloadId;
    }

    public void setFkWorkloadId(Long fkWorkloadId) {
        this.fkWorkloadId = fkWorkloadId;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance == null ? null : performance.trim();
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
}