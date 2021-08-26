package com.bdilab.colosseum.benchmark.workloadconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workloadconfig.base.WorkloadConfig;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;

public class RedisBenchmark_4_0_14_WorkloadConfig extends WorkloadConfig {

    // 准备执行测试的命令，包含占位符
    private String prepareCmd;
    // 执行测试的命令，去掉占位符
    private String benchmarkCmd;
    // 读取结果的命令
    private String getResultCmd;
    // SSH需要的参数
    private String ip;
    private String username;
    private String password;

    @Override
    public void generate() {
        String workloadLocation = otherParams.get("workLoadPath").toString();

        // 使用"$"占位
        String performanceActual = "$";

        // 测试命令：redis-benchmark -t set/get/...... -c 50 -n 10000 -q > txt.log"
        // -c（clients）选项代表客户端的并发量（默认50）
        // -n（num）选项代表客户端请求数量（默认100000）
        String clients = execParams.get("clients");
        String num = execParams.get("num");

        prepareCmd = workloadLocation + "/src/redis-benchmark -t " + performanceActual +
                " -c " + clients + " -n " + num + " -q > " + workloadLocation + "/redis-bm-result.log";

        //System.out.println(benchmarkCmd);
        getResultCmd = "cat " + workloadLocation + "/redis-bm-result.log";
        System.out.println(getResultCmd);

    }

    @Override
    public Object analyze(SutConfig sutConfig) {
        this.ip = this.otherParams.get("ip").toString();
        this.username = this.otherParams.get("username").toString();
        this.password = this.otherParams.get("password").toString();
        String performance = this.otherParams.get("performance").toString();
        String performanceActual;

        try {
            performanceActual = XlsUtils.readExcel(sutConfig.getPerformanceFilePath(), performance, Constant.ACTUAL_PERFORMANCE_ROW_NUMBER);
            benchmarkCmd = prepareCmd.replace("$", performanceActual);
            System.out.println(benchmarkCmd);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SshUtils.executeReturnSuccess(ip, username, password, benchmarkCmd);

        // 读取"redis-bm-result.log"结果
        String result = SshUtils.executeReturnSuccess(ip, username, password, getResultCmd);
        if (result.isEmpty()) {
            return "No result.";
        }
        String[] strs = result.split(" |\n");
        int length= strs.length;
        return strs[length-4];

    }
}
