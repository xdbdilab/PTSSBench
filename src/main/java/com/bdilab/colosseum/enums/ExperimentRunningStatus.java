package com.bdilab.colosseum.enums;

public enum ExperimentRunningStatus {
    RUNNING(0,"运行中"),
    RUNNINGSUCCESS(1,"运行成功"),
    RUNNINGFAIL(2,"运行失败");

    private Integer value;
    private String status;

    ExperimentRunningStatus(Integer value, String status){
        this.value=value;
        this.status=status;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public static ExperimentRunningStatus getExperimentRunningStatusById(Integer id){
        switch (id){
            case 0:return RUNNING;
            case 1:return RUNNINGSUCCESS;
            case 2:return RUNNINGFAIL;
            default:return null;
        }
    }
}
