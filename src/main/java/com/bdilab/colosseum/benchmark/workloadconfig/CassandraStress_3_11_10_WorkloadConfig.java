package com.bdilab.colosseum.benchmark.workloadconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workloadconfig.base.WorkloadConfig;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;

public class CassandraStress_3_11_10_WorkloadConfig extends WorkloadConfig {

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

        // 测试命令:"cassandra-stress read/write/mixed n=100000 -rate threads=5 > txt.log"
        // n（num）:指定要执行的操作次数
        // -rate:设置写入速度相关的参数
        // threads:并发运行的客户端数量
        String num = execParams.get("num");
        String threads = execParams.get("threads");

        prepareCmd = workloadLocation + "/bin/cassandra-stress " + performanceActual +
                " n=" + num + " -rate threads=" + threads + " > " + workloadLocation + "/cassandra-stress-result.log";

        //System.out.println(benchmarkCmd);
        getResultCmd = "grep \"Op rate\" " + workloadLocation + "/cassandra-stress-result.log";
        System.out.println(getResultCmd);
    }

    @Override
    public Object analyze(SutConfig sutConfig) {
        this.ip = this.otherParams.get("ip").toString();
        this.username = this.otherParams.get("username").toString();
        this.password = this.otherParams.get("password").toString();
        String performance = this.otherParams.get("performance").toString();
        String performanceActual;
        String rawResult;
        String result;
        String opRate;

        try {
            performanceActual = XlsUtils.readExcel(sutConfig.getPerformanceFilePath(), performance, Constant.ACTUAL_PERFORMANCE_ROW_NUMBER);
            benchmarkCmd = prepareCmd.replace("$", performanceActual);
            System.out.println(benchmarkCmd);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SshUtils.executeReturnSuccess(ip, username, password, benchmarkCmd);

        System.out.println(getResultCmd);
        // 读取"cassandra-stress-result.log"结果
        rawResult = SshUtils.executeReturnSuccess(ip, username, password, getResultCmd);
        System.out.println("原始数据：" + rawResult);
        if (rawResult == null) {
            return "No result. Maybe the write/counter-write command should be executed first.";
        }

        // 处理原始结果
        result = rawResult.replaceAll(" ", "").replaceAll(",", "");
        System.out.println(result);
        opRate = result.substring(result.indexOf(":")+1, result.indexOf("op"));
        return opRate;
    }
}
