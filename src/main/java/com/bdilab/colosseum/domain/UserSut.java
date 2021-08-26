package com.bdilab.colosseum.domain;

import lombok.Builder;

import java.util.Date;

@Builder
public class UserSut {
    private Long id;

    private Long fkUserId;

    private String sutName;

    private String sutVersion;

    //设计时遗漏的安装路径
    private String installPath;

    private String parameterFile;

//    private String testShellFile;
    private String testShellCmd;

    private Date createTime;

    private Date modifyTime;

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

    public String getSutName() {
        return sutName;
    }

    public void setSutName(String sutName) {
        this.sutName = sutName == null ? null : sutName.trim();
    }

    public String getSutVersion() {
        return sutVersion;
    }

    public void setSutVersion(String sutVersion) {
        this.sutVersion = sutVersion == null ? null : sutVersion.trim();
    }

    public String getParameterFile() {
        return parameterFile;
    }

    public void setParameterFile(String parameterFile) {
        this.parameterFile = parameterFile == null ? null : parameterFile.trim();
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