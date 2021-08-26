package com.bdilab.colosseum.benchmark.workload.base;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workloadconfig.base.WorkloadConfig;
import com.bdilab.colosseum.utils.MapUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class Workload {
    protected Map<String, Object> params;
    protected SutConfig sutConfig;
    protected WorkloadConfig workloadConfig;
    public Workload(){
        params = new HashMap<>();
    }

    //workloadExecParams在运行试验时，由用户前端指定；commonParams这些参数没有必要传给算法端，放在Workload这个运行时环境里很合适
    public void init(Map<String, String> workloadExecParams, Map<String, Object> commonParams, Map<String, Object> contextParams){
        this.workloadConfig.putExecParams(workloadExecParams);
        this.workloadConfig.putOtherParams(commonParams);
        this.sutConfig.putParams(commonParams);
        this.putParams(commonParams);
        if(contextParams!=null){
            this.putParams(contextParams);
        }
        this.workloadConfig.generate();
    }

    //这里的params是从算法端传递过来的一些参数，比如paramList，也可根据需求存入其它的东西
    public abstract void start(Map<String, Object> params);
    public abstract String getResult();
    public abstract void clean();

    public void setSutConfig(SutConfig sutConfig){
        this.sutConfig = sutConfig;
    }

    public void putParams(Map<String, Object> params){
        MapUtils.putParams(params,this.params);
    }

    public Object getParam(String key){
        return params.get(key);
    }
}
