package com.bdilab.colosseum.benchmark.workload;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workload.base.Workload;
import com.bdilab.colosseum.benchmark.workloadconfig.Sysbench_1_0_20_WorkloadConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sysbench_1_0_20_Workload extends Workload {
    private String result = null;
    public Sysbench_1_0_20_Workload(){
        super();
        this.workloadConfig = new Sysbench_1_0_20_WorkloadConfig();
    }

    @Override
    public void start(Map<String, Object> params) {
        sutConfig.putParams(params);
        workloadConfig.putOtherParams(params);
        sutConfig.remoteConfSet();
        Sysbench_1_0_20_WorkloadConfig sysbench_1_0_20_workloadConfig = (Sysbench_1_0_20_WorkloadConfig)this.workloadConfig;
        List<String> cmdList = new ArrayList<>();
        cmdList.add(sysbench_1_0_20_workloadConfig.getFirstToDo());
        cmdList.add(sysbench_1_0_20_workloadConfig.getOLTPPrepare());
        cmdList.add(sysbench_1_0_20_workloadConfig.getOLTPRun());
        for (String cmd : cmdList) {
            SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), cmd);
        }
        cmdList.clear();
        result = workloadConfig.analyze(sutConfig).toString();
        // 清除测试数据
        cmdList.add(sysbench_1_0_20_workloadConfig.getOLTPClean());
        for (String cmd : cmdList) {
            SshUtils.executeReturnSuccess(this.params.get("ip").toString(), this.params.get("username").toString(), this.params.get("password").toString(), cmd);
        }
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void clean() {
        //一些清除工作
    }
}
