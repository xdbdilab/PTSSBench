package com.bdilab.colosseum.service;

import com.bdilab.colosseum.domain.Hardware;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 17:08
 **/
public interface HardwareService {

    /**
     * 配置硬件环境（桥接式）
     */
    Hardware addHardware(Long userId, String hardwareName, String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername, String hardPwd);

    /**
     * 测试硬件环境连接情况（桥接式）
     */
    boolean testConnect(String hardwareIp, String hardUsername, String hardPwd);

    void deleteHardware(Long hardwareId);

    boolean updateHardware(Long hardwareId, String hardwareName, String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername, String hardPwd);
}
