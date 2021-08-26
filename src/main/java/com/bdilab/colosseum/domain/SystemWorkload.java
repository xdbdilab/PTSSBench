package com.bdilab.colosseum.domain;

public class SystemWorkload {
    private Long id;

    private String workloadName;

    private String workloadVersion;

    private String performance;

//    private String testShellFile;
    private String testShellCmd;

    private String execParams;

    private String logoPath;

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getExecParams() {
        return execParams;
    }

    public void setExecParams(String execParams) {
        this.execParams = execParams;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}