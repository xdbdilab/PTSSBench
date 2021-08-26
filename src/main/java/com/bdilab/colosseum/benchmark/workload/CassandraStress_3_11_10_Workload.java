package com.bdilab.colosseum.benchmark.workload;

import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.benchmark.workloadconfig.CassandraStress_3_11_10_WorkloadConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.util.Map;

public class CassandraStress_3_11_10_Workload extends Workload {

    private String result;

    public CassandraStress_3_11_10_Workload() {
        super();
        this.workloadConfig = new CassandraStress_3_11_10_WorkloadConfig();
    }

    @Override
    public void start(Map<String, Object> params) {
        sutConfig.putParams(params);
        workloadConfig.putOtherParams(params);
        sutConfig.remoteConfSet();
        CassandraStress_3_11_10_WorkloadConfig config = (CassandraStress_3_11_10_WorkloadConfig) this.workloadConfig;
        result = config.analyze(sutConfig).toString();
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void clean() {
        // 关闭cassandra需要使用"kill `cat <pidfile>`"命令（或者"ps -ef | grep cassandra | grep -v grep | awk '{print $2}' | xargs kill -9"）
        String stopCmd = "kill `cat " + this.params.get("sutPath").toString() + "/cassandra.pid`";
        // 删除测试结果
        String deleteResultCmd = "rm -rf " + this.params.get("workLoadPath").toString() + "/cassandra-stress-result.log";

        SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), stopCmd);
        SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), deleteResultCmd);
    }
}
