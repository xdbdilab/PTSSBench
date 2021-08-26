package com.bdilab.colosseum.domain;

import java.util.Date;

public class ExperimentRun {
    private Long id;

    private Long fkExperimentId;

    private String runId;

    private Integer status;

    private String result;

    private Date startTime;

    private Date endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkExperimentId() {
        return fkExperimentId;
    }

    public void setFkExperimentId(Long fkExperimentId) {
        this.fkExperimentId = fkExperimentId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId == null ? null : runId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}