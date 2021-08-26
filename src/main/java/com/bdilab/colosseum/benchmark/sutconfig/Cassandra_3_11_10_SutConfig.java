package com.bdilab.colosseum.benchmark.sutconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.ConfSutConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.util.ArrayList;
import java.util.List;

public class Cassandra_3_11_10_SutConfig extends ConfSutConfig {
    private String sutParamsFilePath;

    @Override
    public void setPerformanceFilePath(String sutParamsFilePath) {
        this.sutParamsFilePath = sutParamsFilePath;
    }

    @Override
    public String getPerformanceFilePath() {
        return this.sutParamsFilePath;
    }

    @Override
    public boolean remoteConfSet() {
        try {
            String ip = params.get("ip").toString();
            String username = params.get("username").toString();
            String password = params.get("password").toString();
            String paramList = params.get("paramList").toString();
            String sutLocation = params.get("sutPath").toString();
            String yamlPath = System.getProperty("user.dir")+"/file/sys/template/conf/cassandra.yaml";
            String yamlFile;

            List<String> cmdList = new ArrayList<>();

            // 替换yaml文件模板参数
            yamlFile = buildSutYaml(yamlPath, paramList);

            // 根据生成的yaml文件覆盖远程yaml文件
            // 运行时使用"../bin/cassandra -p <pidfile>"保存pid文件
            // 遇到"Cassandra 3.0 and later require Java 8u40 or later"问题
            // 在../bin/cassandra中第二行加"JAVA_HOME/path/of/your/jdk"
            // 确定服务器没有cassandra启动，否则启动多个cassandra会卡死服务器
            String yamlSetCmd = "echo -e " + "\"" + yamlFile + "\"" + " > " + sutLocation + "/conf/cassandra.yaml";
            String startCmd = sutLocation + "/bin/cassandra -p " + sutLocation + "/cassandra.pid";

            cmdList.add(yamlSetCmd);
            cmdList.add(startCmd);

            for (String cmd : cmdList) {
                SshUtils.executeReturnSuccess(ip, username, password, cmd);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
