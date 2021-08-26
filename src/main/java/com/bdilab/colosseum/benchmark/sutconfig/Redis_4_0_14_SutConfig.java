package com.bdilab.colosseum.benchmark.sutconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.ConfSutConfig;
import com.bdilab.colosseum.benchmark.sutconfig.base.SutConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.util.ArrayList;
import java.util.List;

public class Redis_4_0_14_SutConfig extends SutConfig {
    private String sutParamsFilePath;

    @Override
    public void setPerformanceFilePath(String sutParamsFilePath) {
        this.sutParamsFilePath = sutParamsFilePath;
    }

    @Override
    public String getPerformanceFilePath() {
        return sutParamsFilePath;
    }

    @Override
    public boolean remoteConfSet() {
        try {
            String ip = params.get("ip").toString();
            String username = params.get("username").toString();
            String password = params.get("password").toString();
            String paramList = params.get("paramList").toString();
            String workloadLocation = params.get("workLoadPath").toString();

            // 重启redis以还原初始配置启动redis
            // 需要开启redis的守护进程设置
            String startCmd = workloadLocation + "/src/redis-server " + workloadLocation + "/redis.conf";

            // 基准测试命令集合
            List<String> cmdList = new ArrayList<>();

            cmdList.add(startCmd);

            // 根据参数列表对redis配置进行修改,redis-cli set ...
            // 生成参数列表,修改redis的配置
            String[] params = paramList.split(",");
            for (String param : params) {
                String[] args = param.split("=");
                String cmd = workloadLocation + "/src/redis-cli config set " + args[0] + " " + args[1];
                cmdList.add(cmd);
            }
            for(String cmd : cmdList){
                //System.out.println(cmd);
                SshUtils.executeReturnSuccess(ip, username, password, cmd);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
