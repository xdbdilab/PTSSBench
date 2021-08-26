package com.bdilab.colosseum.benchmark.workloadconfig.base;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.utils.MapUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class WorkloadConfig {
    //Map的value泛型类型后面可以根据需求改成Object类型，方便存储对象
    protected Map<String, String> execParams; //例如 --tables=10 --threads=20这些
    protected Map<String, Object> otherParams;// 其他参数，例如sutLocation，workloadLocation，paramList

    public WorkloadConfig() {
        execParams = new HashMap<>();
        otherParams = new HashMap<>();
    }

    public void putExecParams(Map<String, String> execParams){
        MapUtils.putParams(execParams,this.execParams);
    }
    public void putOtherParams(Map<String, Object> otherParams){
        MapUtils.putParams(otherParams,this.otherParams);
    }

    public abstract void generate();

    public abstract Object analyze(SutConfig sutConfig);
}