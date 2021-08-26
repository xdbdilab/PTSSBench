package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.UserSut;
import com.bdilab.colosseum.domain.SystemSut;
import com.bdilab.colosseum.vo.ParameterVo;
import com.bdilab.colosseum.vo.SystemSutVo;
import org.springframework.web.multipart.MultipartFile;

import javax.management.InstanceNotFoundException;
import java.rmi.ServerException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 16:51
 **/
public interface SutService {

    /**
     * 配置待调优系统（桥接式）
     * @return
     */
    UserSut addUserSut(Long userId, String sutName, String sutVersion, String installPath, String testShellCmd);

    /**
     * 测试待调优系统是否可用（桥接式）
     */
    boolean testSut(boolean custom, String hardwareIp, String hardUsername, String hardPwd, String sutName, String sutVersion, String sutInstallPath, String testCmd);

    List<SystemSutVo> getSystemSutList();

    /**
     * 获取待调优系统库
     * @param sutName
     * @param sutVersion
     * @param parameterFile
     * @param testShellCmd
     * @return
     */
    SystemSut addSystemSut(String sutName, String sutVersion, MultipartFile parameterFile, String testShellCmd,String workloads,MultipartFile logo);

    /**
     * 编辑待调优系统库
     * @param systemSutId
     * @param sutName
     * @param sutVersion
     * @param parameterFile
     * @param testShellCmd
     */
    SystemSut updateSystemSut(Long systemSutId, String sutName, String sutVersion, MultipartFile parameterFile, String testShellCmd, MultipartFile logo,String workloads);

    /**
     * 删除待调优系统
     * @param systemSutId
     * @return
     */
    int deleteSystemSut(Long systemSutId);

    /**
     * 获取指定环境的默认可选参数集合
     * @param systemEnvId
     * @return
     */
    List<ParameterVo> getSystemEnvParamsDefault(Long userId, Long systemEnvId) throws InstanceNotFoundException, ServerException;

    /**
     * 获取指定环境已配置的参数集合
     * @return
     * @param userId
     * @param systemEnvId
     */
    List<ParameterVo> getSystemEnvParams(Long userId, Long systemEnvId) throws InstanceNotFoundException;

    /**
     * 保存环境的参数配置
     *
     * @param userId
     * @param systemEnvId
     * @param params
     * @return
     */
    int saveSystemEnvParams(Long userId, Long systemEnvId, String params) throws InstanceNotFoundException;

    /**
     * 上传参数配置文件
     * @param userId
     * @param systemEnvId
     * @param paramFile
     */
    int uploadSystemEnvParamFile(Long userId, Long systemEnvId, MultipartFile paramFile) throws InstanceNotFoundException, ServerException;


    void deleteUserSut(Long sutId);

    void updateUserSut(Long sutId, String sutName, String sutVersion, String sutInstallPath, String sutTestCmd);

    List<SystemSut> getAllSystemSut();

    /**
     * 通过sutName和sutVersion获取从参数文件中获取参数列表
     * @param sutName
     * @param sutVersion
     * @return
     * @throws Exception
     */
    List<ParameterVo> getParameterListDefault(String sutName,String sutVersion)throws Exception;
}
