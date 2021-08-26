package com.bdilab.colosseum.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
public class UserWorkload {
    private Long id;

    private Long fkUserId;

    private String workloadName;

    private String workloadVersion;

    //设计时遗漏的安装路径
    private String installPath;

    private String performance;

//    private String testShellFile;
    private String testShellCmd;

    private Date createTime;

    private Date modifyTime;

    private String execParams;

    public String getExecParams() {
        return execParams;
    }

    public void setExecParams(String execParams) {
        this.execParams = execParams;
    }

    public UserWorkload(Long id, Long fkUserId, String workloadName, String workloadVersion, String installPath, String performance, String testShellCmd, Date createTime, Date modifyTime, String execParams) {
        this.id = id;
        this.fkUserId = fkUserId;
        this.workloadName = workloadName;
        this.workloadVersion = workloadVersion;
        this.installPath = installPath;
        this.performance = performance;
        this.testShellCmd = testShellCmd;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.execParams = execParams;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getWorkloadName() {
        return workloadName;
    }

    public void setWorkloadName(String workloadName) {
        this.workloadName = workloadName == null ? null : workloadName.trim();
    }

    public String getWorkloadVersion() {
        return workloadVersion;
    }

    public void setWorkloadVersion(String workloadVersion) {
        this.workloadVersion = workloadVersion == null ? null : workloadVersion.trim();
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance == null ? null : performance.trim();
    }

    public String getTestShellCmd() {
        return testShellCmd;
    }

    public void setTestShellCmd(String testShellCmd) {
        this.testShellCmd = testShellCmd;
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

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

}