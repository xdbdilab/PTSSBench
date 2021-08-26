package com.bdilab.colosseum.benchmark.workloadconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.benchmark.workloadconfig.base.WorkloadConfig;
import com.bdilab.colosseum.global.Constant;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;

public class Sysbench_1_0_20_WorkloadConfig extends WorkloadConfig {
    //测试之前必要的工作
    private String FirstToDo;
    // 准备数据
    private String OLTPPrepare;
    // 执行测试
    private String OLTPRun;
    // 清理数据
    private String OLTPClean;

    private String outputPath;

    @Override
    public void generate() {
        String workloadLocation = otherParams.get("workLoadPath").toString();
        String sutLocation = otherParams.get("sutPath").toString();
        // TODO 这里如果没有mysql-exp这个文件夹，是不会自动创建的，不如直接扔到data目录下
        String outputDirPath = sutLocation + "/mysql-exp";
        outputPath = outputDirPath + "/mysysbench.log";

        //TODO 这些参数也存入数据库，由管理员添加；后续也可以把这些workload语句扔到模板里，使用跟SutConfig相同的方式进行参数替换
        String tables = execParams.get("tables");
        String tableSize = execParams.get("table_size");
        String threads = execParams.get("threads");
        String mysqlDb = "sys_test";
        FirstToDo = "mkdir -p " + outputDirPath + " & "+sutLocation+"/bin/mysql -h localhost -P3306 -uroot -p'123456' -se \"create database if not exists "+mysqlDb+";\"";
        OLTPPrepare = workloadLocation + "/bin/sysbench " + workloadLocation + "/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables="+tables+" --table-size="+tableSize+" --threads="+threads+" --time=100 --report-interval=10 --mysql-db="+ mysqlDb+" --mysql-socket="+sutLocation+"/mysql.sock prepare";
        OLTPRun = workloadLocation + "/bin/sysbench " + workloadLocation + "/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables="+tables+" --table-size="+tableSize+" --threads="+threads+" --time=100 --report-interval=10 --mysql-db="+mysqlDb+" --mysql-socket="+sutLocation+"/mysql.sock run > " + outputPath;
        OLTPClean = workloadLocation + "/bin/sysbench " + workloadLocation + "/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables="+tables+" --table-size="+tableSize+" --threads="+threads+" --time=100 --report-interval=10 --mysql-db="+mysqlDb+" --mysql-socket="+sutLocation+"/mysql.sock cleanup";
    }

    @Override
    public String analyze(SutConfig sutConfig) {
        String avg = null;
        String mysqlResult = null;
        String performance = this.otherParams.get("performance").toString();
        String ip = this.otherParams.get("ip").toString();
        String username = this.otherParams.get("username").toString();
        String password = this.otherParams.get("password").toString();
        // 读取测试结果
        // 根据用户传递的performance对应在xxx-performance.xls中找到对应actual_performance
        //     TODO 这里的参数1也要封装，注入到spring或者其它方式
        String performanceActual = null;
        try {
            performanceActual = XlsUtils.readExcel(sutConfig.getPerformanceFilePath() ,performance, Constant.ACTUAL_PERFORMANCE_ROW_NUMBER).replace(".0","");
        } catch (Exception e) {
            performanceActual = "performance_not_found-colosseum";
            e.printStackTrace();
        }
        String resultCmd = "grep " + performanceActual + ": " + outputPath;
        String[] split = SshUtils.executeReturnSuccess(ip, username, password, resultCmd).replaceAll(" ", "").split(":");
        if(split.length < 2)return "";
        avg = split[1];
        System.out.println("工作负载基准测试结果:" + avg);
        if (avg == null || avg.equals(""))return "";
        if (avg.contains("/")) {
            mysqlResult = avg.replace(avg.substring(avg.lastIndexOf("/"),avg.length()),"");
            System.out.println("性能指标值：" + mysqlResult);
        }
        // 对于特殊的几个性能指标特殊处理
        else if ("transactions_per_sec".equals(performance) || "queries_per_sec".equals(performance)
                || "ignored_errors_per_sec".equals(performance) || "reconnects_per_sec".equals(performance)) {
            // subString左闭右开
            mysqlResult = avg.substring(avg.indexOf("(")+1,avg.indexOf("per"));
            System.out.println("性能指标值：" + mysqlResult);
        }
        else if ("transactions".equals(performance) || "queries".equals(performance)
                || "ignored_errors".equals(performance) || "reconnects".equals(performance)) {
            mysqlResult = avg.substring(0,avg.indexOf("("));
            System.out.println("性能指标值：" + mysqlResult);
        }
        else{
            mysqlResult = avg;
        }
        return mysqlResult.trim();
    }

    public String getOLTPPrepare() {
        return OLTPPrepare;
    }

    public String getOLTPRun() {
        return OLTPRun;
    }

    public String getOLTPClean() {
        return OLTPClean;
    }

    public String getFirstToDo(){
        return FirstToDo;
    }

}
