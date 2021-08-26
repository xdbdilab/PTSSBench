package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.vo.SystemEnvDetailVo;
import com.bdilab.colosseum.vo.SystemEnvVO;
import org.springframework.web.multipart.MultipartFile;

import javax.management.InstanceNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 17:26
 **/
public interface SystemEnvService {

    /**
     * 获取系统环境列表
     */
    List<SystemEnv> getSystemEnvList(Long userId)throws Exception;

    /**
     * 添加系统环境(参数待修改，安装路径没有)
     */
    SystemEnv addSystemEnv(Long userId, String envName, String envDesc, String hardwareName, String hardwareDesc,
                           Byte hardType, String hardwareIp, String hardUsername, String hardPwd, String sutName,
                           String sutVersion, String sutInstallPath, String sutTestCmd, String parameters,
                           String workloadName, String workloadVersion, String workloadInstallPath,
                           String workloadTestCmd, String performances, MultipartFile logo);

    void deleteSystemEnv(Long userId, Long systemEnvId) throws InstanceNotFoundException;

    SystemEnv updateSystemEnv(Long userId, Long systemEnvId, String envName, String envDesc, String hardwareName,
                              String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername,
                              String hardPwd, String sutName, String sutVersion, String sutInstallPath,
                              String sutTestCmd, String parameters,String workloadName, String workloadVersion,
                              String workloadInstallPath, String workloadTestCmd,String performances,MultipartFile logo) throws InstanceNotFoundException;

    SystemEnvDetailVo getSystemEnvInfo(Long userId, Long systemEnvId) throws Exception;

    /**
     * 根据环境id判断环境是否存在
     * @param envId
     * @return
     */
    boolean checkBySystemEnvId(Long envId);
}
