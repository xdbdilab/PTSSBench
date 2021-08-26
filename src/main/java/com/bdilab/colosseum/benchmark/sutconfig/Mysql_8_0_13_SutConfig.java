package com.bdilab.colosseum.benchmark.sutconfig;

import com.bdilab.colosseum.benchmark.sutconfig.base.ConfSutConfig;
import com.bdilab.colosseum.utils.SshUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mysql_8_0_13_SutConfig extends ConfSutConfig {
    private String sutParamsFilePath;

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
            String paramList = params.get("paramList").toString();
            String sutLocation = params.get("sutPath").toString();

            // 基准测试命令集合
            List<String> cmdList = new ArrayList<>();

            //将模板的待替换参数用实际参数替换，并得到结果
            // TODO 文件路径也要存入数据库
            String ConfPath = System.getProperty("user.dir")+"/file/sys/template/conf/mysqlConf";
            ArrayList<String> needToReplace = new ArrayList<>();
            ArrayList<String> replaceBy = new ArrayList<>();
            //################ TODO 下面这些参数最终也要存入数据库#################
            needToReplace.add("paramList_need_to_replace_by_colosseum");
            needToReplace.add("sutLocation_need_to_replace_by_colosseum");
            replaceBy.add(paramList.replaceAll(",","\n"));
            replaceBy.add(sutLocation);
            String confString = buildSutConf(ConfPath,needToReplace,replaceBy);
            //#################################################################

            // 根据生成的配置覆盖远程配置文件
            String coverRemoteConfigFile = "echo -e "+"\""+confString+"\""+" > /etc/my.cnf";
            cmdList.add(coverRemoteConfigFile);

            // 重启mysql
            String restartCmd = "service mysql restart";
            cmdList.add(restartCmd);

            for (String cmd : cmdList) {
                SshUtils.executeReturnSuccess(params.get("ip").toString(), params.get("username").toString(), params.get("password").toString(), cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
