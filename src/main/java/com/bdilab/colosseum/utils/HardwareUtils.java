package com.bdilab.colosseum.utils;

import com.bdilab.colosseum.domain.OperatingSystem;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/16 10:27
 */
public class HardwareUtils {
    //获取硬件环境的cpu、memory、disk、bandwidth、kernel、os

    public static Map<String,String> getHardwareInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        Map<String,String> hardInfo = new HashMap<>();
        Map<String,String> osInfo = getOsInfo(hardwareIp,hardwareUsername,hardwarePwd);
        hardInfo.putAll(osInfo);
        hardInfo.put("bandwidth",getBandWidthInfo(hardwareIp,hardwareUsername,hardwarePwd));
        hardInfo.put("cpu",getCpuInfo(hardwareIp,hardwareUsername,hardwarePwd));
        hardInfo.put("disk",getDiskInfo(hardwareIp,hardwareUsername,hardwarePwd));
        hardInfo.put("memory",getMemoryInfo(hardwareIp,hardwareUsername,hardwarePwd));
        hardInfo.put("kernel",getKernelInfo(hardwareIp,hardwareUsername,hardwarePwd));
        return hardInfo;
    }
    /**
     * 获取操作系统信息
     */
    public static Map<String,String> getOsInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        Map<String,String> osInfo = new HashMap<>();
        String cmd = "cat /etc/issue|sed -n 1, 1p|awk '{print $1,$3}'";

        String result = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        if (StringUtils.isEmpty(result)) {
            cmd = "cat /etc/redhat-release|awk '{print $1,$4}'";
            result = SshUtils.execute(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        }
        if (!StringUtils.isEmpty(result)) {
            System.out.println(result);
            String[] osInfos = result.replace("\n","").split(" ");
            osInfo.put("osName",osInfos[0]);
            osInfo.put("osVersion",osInfos[1]);
        }else {
            osInfo = null;
        }
        return osInfo;
    }

    /**
     * 获取系统的网络带宽
     */
    public static String getBandWidthInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        String bandwidth = null;
        String ethNameGetCmd = "ifconfig|sed -n 1, 1p| awk '{print $1}'";
        String ethResult = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, ethNameGetCmd);
        if (!StringUtils.isEmpty(ethResult)){
            String bandWidthCmd = "ethtool "+ ethResult.replace("\n","") +"|awk '{if($0 ~ /Speed/) print $2}'";
            String bandWidthResult = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, bandWidthCmd);
            if (!StringUtils.isEmpty(bandWidthResult)){
                System.out.println(bandWidthResult);
                bandwidth = bandWidthResult.replace("\n","");
            }
        }
        return bandwidth;
    }

    /**
     * 获取系统的cpu个数
     */
    public static String getCpuInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        String cpu = null;
        String cmd = "lscpu |grep 'Socket(s)'|awk '{print $2}'";
        String result = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            cpu = result.replace("\n","");
        }
        return cpu;
    }

    /**
     * 获取系统的内核核数
     */
    public static String getKernelInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        String kernel = null;
        String cmd = "lscpu | grep 'CPU(s)'|sed -n 1,1p|awk '{print $2}'";
        String result = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            kernel = result.replace("\n","");
        }
        return kernel;
    }

    /**
     * 获取系统的内存大小
     */
    public static String getMemoryInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        String memory = null;
        String cmd = "free |sed -n 2,2p|awk '{print $2}'";
        String result = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            memory = result.replace("\n","")+" kb";
        }
        return memory;
    }


    /**
     * 获取系统的硬盘大小
     */
    public static String getDiskInfo(String hardwareIp, String hardwareUsername, String hardwarePwd){
        String disk = null;
        String cmd = "fdisk -l |grep Disk|sed -n 1,1p|awk '{print $5}'";
        String result = SshUtils.executeReturnSuccess(hardwareIp,hardwareUsername,hardwarePwd, cmd);
        if (!StringUtils.isEmpty(result)){
            System.out.println(result);
            disk = result.replace("\n","")+"bytes";
        }
        return disk;
    }

}
