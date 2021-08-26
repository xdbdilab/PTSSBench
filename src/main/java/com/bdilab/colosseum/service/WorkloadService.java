package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.domain.UserWorkload;
import com.bdilab.colosseum.vo.PerformanceVo;
import com.bdilab.colosseum.vo.SystemWorkloadVo;
import org.springframework.web.multipart.MultipartFile;

import javax.management.InstanceNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 17:09
 **/
public interface WorkloadService {
    /**
     * 添加工作负载
     */
    UserWorkload addUserWorkload(Long userId, String workloadName, String workloadVersion, String installPath, String testShellCmd);

    /**
     * 测试工作负载可用情况
     * @param hardwareIp
     * @param hardUsername
     * @param hardPwd
     * @param workloadName
     * @param workloadVersion
     * @param workloadInstallPath
     * @param testCmd
     */
    Map<String,Object> testWorkload(String hardwareIp, String hardUsername, String hardPwd, String workloadName, String workloadVersion, String workloadInstallPath, String testCmd) throws InstanceNotFoundException;

    /**
     * 添加系统的工作负载
     * @param userId
     * @param workloadName
     * @param workloadVersion
     * @param performance
     * @param testShellCmd
     * @return
     */
    SystemWorkload addSystemWorkload(Long userId, String workloadName, String workloadVersion, MultipartFile performance, String testShellCmd, String execParams,MultipartFile logo);

    /**
     * 获取系统支持的工作负载列表
     */
    List<SystemWorkloadVo> getSystemWorkloadList();

    /**
     * 获取系统默认的可选性能指标集合
     *
     * @param userId
     * @param systemEnvId
     * @return
     */
    String getSystemEnvPerformanceDefault(Long userId, Long systemEnvId) throws InstanceNotFoundException;

    /**
     * 获取环境的性能指标集合
     *
     * @param userId
     * @param systemEnvId
     * @return
     */
    String getSystemEnvPerformance(Long userId, Long systemEnvId) throws InstanceNotFoundException;

    /**
     * 配置环境的性能指标
     * @param userId
     * @param systemEnvId
     * @param performances
     */
    int saveSystemEnvPerformance(Long userId, Long systemEnvId, String performances) throws InstanceNotFoundException;

    void deleteUserWorkload(Long workloadId);

    void updateUserWorkload(Long workloadId, String workloadName, String workloadVersion, String workloadInstallPath, String workloadTestCmd);

    void deleteSystemWorkload(Long userId, Long workloadId);

    SystemWorkload updateSystemWorkload(Long userId, Long workloadId, String workloadName, String workloadVersion, MultipartFile performance, String testShellCmd,String execParams,MultipartFile logo);

    int setPerformanceDefault(Long userId, Long systemEnvId, String performances) throws InstanceNotFoundException;

    List<SystemWorkload> getAllSystemWorkload();

    List<SystemWorkloadVo> getWorkloads(String workloads);

    List<PerformanceVo> getPerformanceListDefault(String workloadName, String workloadVersion) throws Exception;
}
