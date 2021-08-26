package com.bdilab.colosseum.service.impl;

import com.bdilab.colosseum.domain.Hardware;
import com.bdilab.colosseum.domain.OperatingSystem;
import com.bdilab.colosseum.mapper.HardwareMapper;
import com.bdilab.colosseum.mapper.OperatingSystemMapper;
import com.bdilab.colosseum.service.HardwareService;
import com.bdilab.colosseum.utils.HardwareUtils;
import com.bdilab.colosseum.utils.SshUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import java.util.Date;
import java.util.Map;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 9:58
 **/
@Service
public class HardwareServiceImpl implements HardwareService {
    private static final Logger log = LoggerFactory.getLogger(HardwareServiceImpl.class);

    @Resource
    HardwareMapper hardwareMapper;
    @Resource
    OperatingSystemMapper operatingSystemMapper;

    @Override
    public Hardware addHardware(Long userId, String hardwareName, String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername, String hardPwd) {
        //获取硬件信息
        Map<String, String> hardwareInfo = HardwareUtils.getHardwareInfo(hardwareIp, hardUsername, hardPwd);
        OperatingSystem operatingSystem = operatingSystemMapper.selectByNameAndVersion(hardwareInfo.get("osName"), hardwareInfo.get("osVersion"));
        if (operatingSystem == null) {
            operatingSystem = OperatingSystem.builder()
                    .osName(hardwareInfo.get("osName"))
                    .version(hardwareInfo.get("osVersion"))
                    .build();
            int insertOs = operatingSystemMapper.insert(operatingSystem);
            if (insertOs <= 0) {
                log.error("插入一条os记录失败:{}", operatingSystem.getId());
                return null;
            }
        }
        Hardware hardware = Hardware.builder()
                .fkUserId(userId)
                .hardwareName(hardwareName)
                .description(hardwareDesc)
                .type(hardType)
                .ip(hardwareIp)
                .username(hardUsername)
                .password(hardPwd)
                .fkOsId(operatingSystem.getId())
                .cpu(hardwareInfo.get("cpu"))
                .bandwidth(hardwareInfo.get("bandwidth"))
                .disk(hardwareInfo.get("disk"))
                .memory(hardwareInfo.get("memory"))
                .kernel(hardwareInfo.get("kernel"))
                .createTime(new Date())
                .build();

        int insert = hardwareMapper.insert(hardware);
        if (insert > 0) {
            log.info("成功插入一条hardware记录:{}", hardware.getId());
            return hardware;
        } else {
            log.error("插入一条hardware记录失败:{}", hardware.getId());
        }
        return null;
    }

    @Override
    public boolean testConnect(String hardwareIp, String hardUsername, String hardPwd) {
        return SshUtils.testConnect(hardwareIp,hardUsername,hardPwd);
    }

    @Override
    public void deleteHardware(Long fkHardwareId) {
        hardwareMapper.deleteByPrimaryKey(fkHardwareId);
    }

    @Override
    public boolean updateHardware(Long hardwareId, String hardwareName, String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername, String hardPwd) {
        Hardware hardware = hardwareMapper.selectByPrimaryKey(hardwareId);

        //获取硬件信息
        Map<String, String> hardwareInfo = HardwareUtils.getHardwareInfo(hardwareIp, hardUsername, hardPwd);
        OperatingSystem operatingSystem = operatingSystemMapper.selectByNameAndVersion(hardwareInfo.get("osName"), hardwareInfo.get("osVersion"));
        if (operatingSystem == null) {
            operatingSystem = OperatingSystem.builder()
                    .osName(hardwareInfo.get("osName"))
                    .version(hardwareInfo.get("osVersion"))
                    .build();
            int insertOs = operatingSystemMapper.insert(operatingSystem);
            if (insertOs <= 0) {
                log.error("插入一条os记录失败:{}", operatingSystem.getId());
                return false;
            }
        }
        hardware.setHardwareName(hardwareName);
        hardware.setDescription(hardwareDesc);
        hardware.setType(hardType);
        hardware.setIp(hardwareIp);
        hardware.setUsername(hardUsername);
        hardware.setPassword(hardPwd);
        hardware.setFkOsId(operatingSystem.getId());
        hardware.setCpu(hardwareInfo.get("cpu"));
        hardware.setBandwidth(hardwareInfo.get("bandwidth"));
        hardware.setDisk(hardwareInfo.get("disk"));
        hardware.setMemory(hardwareInfo.get("memory"));
        hardware.setKernel(hardwareInfo.get("kernel"));
        hardware.setModifyTime(new Date());

        hardwareMapper.updateByPrimaryKeySelective(hardware);
        return true;
    }
}
