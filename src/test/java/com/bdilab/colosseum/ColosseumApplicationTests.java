package com.bdilab.colosseum;

import com.bdilab.colosseum.mapper.AlgorithmMapper;
import com.bdilab.colosseum.service.ExperimentService;
import com.bdilab.colosseum.utils.CommandUtils;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import com.bdilab.colosseum.vo.ParameterVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

import static java.lang.Thread.sleep;

@SpringBootTest
class ColosseumApplicationTests {
    @Autowired
    ExperimentService experimentService;

    @Autowired
    AlgorithmMapper algorithmMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testSshConnect(){
        //成功测试
        boolean flag = SshUtils.testConnect("192.168.0.76","root","123456");
        System.out.println(flag);
        //失败测试
        boolean flag2 = SshUtils.testConnect("192.168.0.76","root","12345");
        System.out.println(flag2);
        boolean flag3 = SshUtils.testConnect("192.168.0.77","root","123456");
        System.out.println(flag3);
    }

    @Test
    void testSshExecCmd(){
        String cmd = "cd /etc/init.d;./mysql status";
        String result = SshUtils.execute("192.168.0.76","root","123456",cmd);
        System.out.println(result);
    }

    @Test
    void testGetOsInfo(){
        Map<String,String> osInfo = new HashMap<>();
        String cmd = "cat /etc/issue|sed -n \"1, 1p\"";
        String result = SshUtils.executeReturnSuccess("192.168.0.76","root","123456", cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            String[] osInfos = result.replace("\n","").split(" release ");
            osInfo.put("osName",osInfos[0]);
            osInfo.put("osVersion",osInfos[1]);
        }
    }

    @Test
    void testGetBandwidthInfo(){
        String bandwidth = null;
        String ethNameGetCmd = "ifconfig|sed -n \"1, 1p\"| awk '{print $1}'";
        String ethResult = SshUtils.executeReturnSuccess("192.168.0.76","root","123456", ethNameGetCmd);
        if (!StringUtils.isEmpty(ethResult)){
            String bandWidthCmd = "ethtool "+ ethResult.replace("\n","") +"|awk '{if($0 ~ /Speed/) print $2}'";
            String bandWidthResult = SshUtils.executeReturnSuccess("192.168.0.76","root","123456", bandWidthCmd);
            if (!StringUtils.isEmpty(bandWidthResult)){
                System.out.println(bandWidthResult);
            }
        }
    }

    @Test
    void testGetKernelInfo(){
        String cpu = null;
        String cmd = "lscpu | grep 'CPU(s)'|sed -n \"1,1p\"|awk '{print $2}'";
        String result = SshUtils.executeReturnSuccess("192.168.0.76","root","123456", cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            cpu = result;
        }
    }
    @Test
    void testGetMemoryInfo(){
        String memory = null;
        String cmd = "free |sed -n \"2,2p\"|awk '{print $2}'\n";
        String result = SshUtils.executeReturnSuccess("192.168.0.76","root","123456", cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            memory = result;
        }
    }

    @Test
    void testUpload(){
        SshUtils.upload("47.105.158.179", "root", "bdilab@1308", "C:\\Users\\cici\\Desktop\\myCnf", "/etc");
    }

    @Test
    void testReplaceYaml() throws IOException, InterruptedException {
        String yamlPath = System.getProperty("user.dir")+"/file/sys/template/conf/cassandra.yaml";
        String paramList = "hinted_handoff_enabled=false,disk_failure_policy=die";
        String[] params = paramList.split(",");
        Map<String, String> args = new HashMap<>();
        for (String param : params) {
            String[] paramValue = param.split("=");
            args.put(paramValue[0], paramValue[1]);
        }
//        Map<String, String> args = new HashMap<>();
//        args.put("num_tokens", "512");
//        args.put("hinted_handoff_enabled", "false");
//        args.put("disk_failure_policy", "die");
        File my = new File(yamlPath);
        BufferedReader br = new BufferedReader(new FileReader(my));
        StringBuilder confStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            String[] yaml = line.split(": ");
            for (Map.Entry<String, String> arg : args.entrySet()) {
                if (yaml[0].equals(arg.getKey())) {
                    yaml[1] = arg.getValue();
                    line = yaml[0] + ": " + yaml[1];
                }
            }
            confStringBuilder.append(line);
            confStringBuilder.append("\n");
        }
//        System.out.println(confStringBuilder.toString());

        String sutLocation = "/home/cass_stress/apache-cassandra-3.11.10";
//        String sutLocation = "/home/hcyong/apache-cassandra-3.11.10";
        String yamlFile = confStringBuilder.toString();

        String stopCmd = "kill `cat " + sutLocation + "/cassandra.pid`";
        String yamlSetCmd = "echo -e " + "\"" + yamlFile + "\"" + " > " + sutLocation + "/cassandra.yaml";
        String startCmd = sutLocation + "/bin/cassandra -p " + sutLocation + "/cassandra.pid";
        System.out.println(stopCmd);
//        System.out.println(yamlSetCmd);
        System.out.println(startCmd);
        List<String> cmdList = new ArrayList<>();
        cmdList.add(stopCmd);
//        cmdList.add(yamlSetCmd);
        cmdList.add(startCmd);
        String hello = "echo hello";
        cmdList.add(hello);

        // ps -ef | grep cassandra | grep -v grep | awk '{print $2}' | xargs kill -9
        for (String cmd : cmdList) {
            System.out.println(SshUtils.executeReturnSuccess("47.105.133.193", "cass_stress", "bdilab@1308", cmd));
//            System.out.println(SshUtils.executeReturnSuccess("192.168.126.130", "hcyong", "123123", cmd));
            sleep(2000);
        }
    }

    @Test
    void testCassandraResult() throws IOException{
        String resultPath = "/home/hcyong/apache-cassandra-3.11.10/tools/bin/test.log";
        String getResultCmd = "grep \"Op rate\" " + resultPath;
        String rawResult = SshUtils.executeReturnSuccess("192.168.126.130", "hcyong", "123123", getResultCmd);
        String result = rawResult.replaceAll(" ", "").replaceAll(",", "");
        String opRate = result.substring(result.indexOf(":")+1, result.indexOf("op"));
        System.out.println(opRate);
    }

    @Test
    void testExperimentService() {
        List<Long> algorithmIds = new ArrayList<>();
        algorithmIds.add(55L);
        algorithmIds.add(56L);
        algorithmIds.add(57L);
        boolean b = experimentService.checkAlgorithms(algorithmIds, 1L);
        System.out.println(b);
    }

}
