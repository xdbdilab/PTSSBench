package com.bdilab.colosseum.benchmark.sutconfig.base;

import com.bdilab.colosseum.utils.MapUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class SutConfig {
    protected Map<String, Object> params;//等同于WorkloadConfig中的otherParams

    public SutConfig() {
        params = new HashMap<>();
    }

    public void putParams(Map<String, Object> params){
        MapUtils.putParams(params,this.params);
    }

    public abstract void setPerformanceFilePath(String sutParamsFilePath);
    public abstract String getPerformanceFilePath();

    public abstract boolean remoteConfSet();
}
