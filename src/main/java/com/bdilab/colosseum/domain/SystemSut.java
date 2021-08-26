package com.bdilab.colosseum.domain;

public class SystemSut {
    private Long id;

    private String sutName;

    private String sutVersion;

    private String parameterFile;

    private String testShellCmd;

    private String logoPath;

    private String workloads;

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getWorkloads() {
        return workloads;
    }

    public void setWorkloads(String workloads) {
        this.workloads = workloads;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}