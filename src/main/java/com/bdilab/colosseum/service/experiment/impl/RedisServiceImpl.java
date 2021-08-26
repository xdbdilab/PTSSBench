package com.bdilab.colosseum.service.experiment.impl;

import com.alibaba.fastjson.JSON;
import com.bdilab.colosseum.bo.SoftwareLocationBO;
import com.bdilab.colosseum.service.experiment.RedisService;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Service
public class RedisServiceImpl implements RedisService {

    private String execRedisBenchmark(String ip, String username, String password,
                                       String sutLocation, String workloadLocation,
                                       String paramList, String performance){
        // 基准测试命令集合
        List<String> cmdList = new ArrayList<>();

        //根据参数列表对redis配置进行修改,redis-cli set ...
        // 生成参数列表,修改redis的配置
        String[] param = paramList.split(",");
        for (int i = 0; i < param.length; i++) {
            String[] args=param[i].split("=");
            String cmd=workloadLocation+"/src/redis-cli set "+args[0]+" "+args[1];
            cmdList.add(cmd);
        }
        for(String c:cmdList){
            System.out.println(c);
            SshUtils.executeReturnSuccess(ip, username, password, c);
        }

        //测试命令：redis-benchmark -t set/get/...... -c 50 -n 10000 -q > txt.log"
        //-c（clients）选项代表客户端的并发量（默认50）
        //-n（num）选项代表客户端请求数量（默认100000）
        String benchmarkCmd;
        try{
            String performanceActual = XlsUtils.readExcel(System.getProperty("user.dir")+"/file/sys/performanceFile/redis-performance.xls",performance,1);
            benchmarkCmd=workloadLocation+"/src/redis-benchmark -t "+performanceActual;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        benchmarkCmd+="-c 50 -n 100000 -q >"+workloadLocation+"/src/txt.log";
        System.out.println(benchmarkCmd);
        SshUtils.executeReturnSuccess(ip, username, password, benchmarkCmd);

        //读取结果
        String resultCmd="cat "+workloadLocation+"/src/txt.log";
        String result=SshUtils.executeReturnSuccess(ip,username,password,resultCmd);
        String[] strs=result.split(" |\n");
        int length= strs.length;
        return strs[length-4];
    }


    @Override
    public String execRedisBenchmark(String softwareLocationBOStr, String paramList, String performance) {
        SoftwareLocationBO softwareLocationBO = JSON.parseObject(softwareLocationBOStr,SoftwareLocationBO.class);
        return execRedisBenchmark(softwareLocationBO.getIp(),
                softwareLocationBO.getUsername(),
                softwareLocationBO.getPassword(),
                softwareLocationBO.getSutPath(),
                softwareLocationBO.getWorkLoadPath(),
                paramList,
                performance);
    }
}