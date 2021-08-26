package com.bdilab.colosseum.benchmark.workload;

import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.benchmark.workloadconfig.RedisBenchmark_4_0_14_WorkloadConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisBenchmark_4_0_14_Workload extends Workload {

    private String result;
    public RedisBenchmark_4_0_14_Workload() {
        super();
        this.workloadConfig = new RedisBenchmark_4_0_14_WorkloadConfig();
    }

    @Override
    public void start(Map<String, Object> params) {
        sutConfig.putParams(params);
        workloadConfig.putOtherParams(params);
        sutConfig.remoteConfSet();
        RedisBenchmark_4_0_14_WorkloadConfig config = (RedisBenchmark_4_0_14_WorkloadConfig) this.workloadConfig;
        result = config.analyze(sutConfig).toString();

    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void clean() {
        String workloadLocation = params.get("workLoadPath").toString();
        // 关闭redis
        String stopCmd = workloadLocation + "/src/redis-cli shutdown";
        // 删除测试结果
        String deleteResultCmd = "rm -rf " + workloadLocation + "/redis-bm-result.log";

        SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), deleteResultCmd);
        SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), stopCmd);
    }
}
