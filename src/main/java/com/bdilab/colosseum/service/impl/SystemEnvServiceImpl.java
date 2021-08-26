package com.bdilab.colosseum.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.colosseum.domain.Hardware;
import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.domain.UserSut;
import com.bdilab.colosseum.domain.UserWorkload;
import com.bdilab.colosseum.mapper.SystemEnvMapper;
import com.bdilab.colosseum.service.HardwareService;
import com.bdilab.colosseum.service.SutService;
import com.bdilab.colosseum.service.SystemEnvService;
import com.bdilab.colosseum.service.WorkloadService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import com.bdilab.colosseum.vo.ParameterVo;
import com.bdilab.colosseum.vo.SystemEnvDetailVo;
import com.bdilab.colosseum.vo.SystemEnvVO;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/14 21:52
 **/
@Service
public class SystemEnvServiceImpl implements SystemEnvService {
    private static final Logger log = LoggerFactory.getLogger(SystemEnvServiceImpl.class);

    @Resource
    SystemEnvMapper systemEnvMapper;
    @Resource
    HardwareService hardwareService;
    @Resource
    SutService sutService;
    @Resource
    WorkloadService workloadService;

    @Value("${logo.path}")
    private String logoPath;

    @Value("${image.map}")
    private String imageMap;

    @Override
    public List<SystemEnv> getSystemEnvList(Long userId)throws Exception {
        List<SystemEnv> systemEnvs = systemEnvMapper.selectByFkUserId(userId);
        for (SystemEnv systemEnv : systemEnvs) {
            if(!systemEnv.getLogoPath().equals("")){
                systemEnv.setLogoPath(imageMap+systemEnv.getLogoPath());
            }
        }
        return systemEnvs;
    }

    @Transactional
    @Override
    public SystemEnv addSystemEnv(Long userId, String envName,String envDesc, String hardwareName,
                                  String hardwareDesc, Byte hardType, String hardwareIp, String hardUsername,
                                  String hardPwd, String sutName, String sutVersion, String sutInstallPath,
                                  String sutTestCmd, String parameters,String workloadName, String workloadVersion,
                                  String workloadInstallPath, String workloadTestCmd,String performances,MultipartFile logo) {
        String path="";
        //如果未上传logo，使用默认logo
        boolean success=true;
        if(logo!=null){
            String logoPath1=this.logoPath+userId;
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemEnv_"+UUID.randomUUID() +"."+strs[strs.length-1];
            success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        if(success){
            SystemEnv systemEnv = new SystemEnv();
            Hardware hardware = hardwareService.addHardware(userId, hardwareName, hardwareDesc, hardType, hardwareIp, hardUsername, hardPwd);
            if (hardware != null){
                UserSut userSut = sutService.addUserSut(userId,sutName,sutVersion,sutInstallPath,sutTestCmd);
                UserWorkload userWorkload = workloadService.addUserWorkload(userId,workloadName,workloadVersion,workloadInstallPath,workloadTestCmd);
                if (userSut != null && userWorkload != null){
                    systemEnv.setEnvName(envName);
                    systemEnv.setEnvDesc(envDesc);
                    systemEnv.setFkUserId(userId);
                    systemEnv.setFkHardwareId(hardware.getId());
                    systemEnv.setFkSutId(userSut.getId());
                    systemEnv.setFkWorkloadId(userWorkload.getId());
                    systemEnv.setCreateTime(new Date());
                    systemEnv.setParameters(parameters);
                    systemEnv.setPerformance(performances);
                    systemEnv.setLogoPath(path);
//                List<ParameterVo> parameterVos=new ArrayList<>();
//                try{
//                    String filePath=userSut.getParameterFile();
//                    parameterVos=XlsUtils.getParamsFromXls((System.getProperty("user.dir") + filePath).replace('/','\\'));
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                JSONArray jsonArray=new JSONArray();
//                for(ParameterVo parameterVo:parameterVos){
//                    LinkedHashMap<String,String> linkedHashMap=new LinkedHashMap();
//                    linkedHashMap.put("name",parameterVo.getName());
//                    linkedHashMap.put("type",parameterVo.getType());
//                    linkedHashMap.put("minValue",parameterVo.getMinValue().toString());
//                    linkedHashMap.put("maxValue",parameterVo.getMaxValue().toString());
//                    linkedHashMap.put("validValue",parameterVo.getValidValue());
//                    linkedHashMap.put("defaultValue",parameterVo.getDefaultValue());
//                    jsonArray.add(linkedHashMap);
//                }
//                systemEnv.setParameters(jsonArray.toJSONString());

                    int insert = systemEnvMapper.insert(systemEnv);
                    if (insert>0){
                        log.info("成功插入一条systemEnv记录:{}",systemEnv.getId());
                        return systemEnv;
                    }
                }
            }
        }

        log.info("插入一条systemEnv失败");
        return null;
    }

    @Transactional
    @Override
    public void deleteSystemEnv(Long userId, Long systemEnvId) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv==null){
            throw new InstanceNotFoundException("环境不存在");
        }
        hardwareService.deleteHardware(systemEnv.getFkHardwareId());
        sutService.deleteUserSut(systemEnv.getFkSutId());
        workloadService.deleteUserWorkload(systemEnv.getFkWorkloadId());
        //删除logo
        String path=systemEnv.getLogoPath();
        if(!path.equals("")){
            File file = new File(path);
            file.delete();
        }
        systemEnvMapper.deleteByPrimaryKey(systemEnv.getId());
    }

    @Transactional
    @Override
    public SystemEnv updateSystemEnv(Long userId, Long systemEnvId, String envName, String envDesc,
                                     String hardwareName, String hardwareDesc, Byte hardType,
                                     String hardwareIp, String hardUsername, String hardPwd,
                                     String sutName, String sutVersion, String sutInstallPath,
                                     String sutTestCmd,String parameters, String workloadName, String workloadVersion,
                                     String workloadInstallPath, String workloadTestCmd,String performances,MultipartFile logo) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv==null){
            throw new InstanceNotFoundException("环境不存在");
        }
        boolean updateHardware = hardwareService.updateHardware(systemEnv.getFkHardwareId(),hardwareName, hardwareDesc, hardType, hardwareIp, hardUsername, hardPwd);
        if (updateHardware){
            sutService.updateUserSut(systemEnv.getFkSutId(),sutName,sutVersion,sutInstallPath,sutTestCmd);
            workloadService.updateUserWorkload(systemEnv.getFkWorkloadId(),workloadName,workloadVersion,workloadInstallPath,workloadTestCmd);
            systemEnv.setEnvName(envName);
            systemEnv.setEnvDesc(envDesc);
            systemEnv.setParameters(parameters);
            systemEnv.setPerformance(performances);
            systemEnv.setModifyTime(new Date());
            boolean success=true;
            String path=null;
            if(logo!=null){
                //删除之前的logo图片
                if (!systemEnv.getLogoPath().equals("")){
                    File file=new File(systemEnv.getLogoPath());
                    file.delete();
                }
                String logoPath1=this.logoPath+"systemEnv";
                String[] strs=logo.getOriginalFilename().split("\\.");
                String fileName="systemEnv_"+UUID.randomUUID() +"."+strs[strs.length-1];
                success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
                path=logoPath1+File.separator+fileName;
            }
            if(success){
                systemEnv.setLogoPath(path);
                int update = systemEnvMapper.updateByPrimaryKeySelective(systemEnv);
                if (update > 0){
                    log.info("成功更新hardware记录:{}",systemEnv.getFkHardwareId());
                    log.info("成功更新userSut记录:{}",systemEnv.getFkSutId());
                    log.info("成功更新userWorkload记录:{}",systemEnv.getFkWorkloadId());
                    log.info("成功更新systemEnv记录:{}",systemEnv.getId());
                    return systemEnv;
                }
            }
        }
        log.info("插入一条systemEnv失败");
        return null;
    }

    @Override
    public SystemEnvDetailVo getSystemEnvInfo(Long userId, Long systemEnvId) throws Exception{

        SystemEnvDetailVo systemEnvDetailVo=systemEnvMapper.selectDetailByIdAndUserId(userId,systemEnvId);
        if(!systemEnvDetailVo.getLogoPath().equals("")){
            systemEnvDetailVo.setLogoPath(imageMap+systemEnvDetailVo.getLogoPath());
        }
        return systemEnvDetailVo;
    }

    /**
     * 根据环境id判断环境是否存在
     *
     * @param envId
     * @return
     */
    @Override
    public boolean checkBySystemEnvId(Long envId) {
        return systemEnvMapper.checkBySystemEnvId(envId) > 0;
    }


}
