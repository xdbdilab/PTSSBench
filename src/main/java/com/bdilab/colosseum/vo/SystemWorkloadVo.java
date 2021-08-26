package com.bdilab.colosseum.vo;

import java.util.List;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 21:39
 */
public class SystemWorkloadVo {
    private String workloadName;

    private List<String> workloadVersion;


    public String getWorkloadName() {
        return workloadName;
    }

    public void setWorkloadName(String workloadName) {
        this.workloadName = workloadName;
    }

    public List<String> getWorkloadVersion() {
        return workloadVersion;
    }

    public void setWorkloadVersion(List<String> workloadVersion) {
        this.workloadVersion = workloadVersion;
    }
}
