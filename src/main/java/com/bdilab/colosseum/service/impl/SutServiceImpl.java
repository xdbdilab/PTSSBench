package com.bdilab.colosseum.service.impl;

import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.domain.SystemSut;
import com.bdilab.colosseum.domain.UserSut;
import com.bdilab.colosseum.mapper.SystemEnvMapper;
import com.bdilab.colosseum.mapper.SystemSutMapper;
import com.bdilab.colosseum.mapper.UserSutMapper;
import com.bdilab.colosseum.service.SutService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.utils.JsonUtils;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import com.bdilab.colosseum.vo.ParameterVo;
import com.bdilab.colosseum.vo.SystemSutVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.ServerException;
import java.util.*;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 16:51
 **/
@Service
public class SutServiceImpl implements SutService {
    private static final Logger log = LoggerFactory.getLogger(SutServiceImpl.class);

    @Autowired
    SystemSutMapper systemSutMapper;
    @Autowired
    UserSutMapper userSutMapper;

    @Autowired
    SystemEnvMapper systemEnvMapper;

    @Value("${logo.path}")
    private String logoPath;

    @Value("${image.map}")
    private String imageMap;

    private String adminParameterFilePath = "/file/sys/parameterFile";

    @Override
    public UserSut addUserSut(Long userId, String sutName, String sutVersion, String installPath, String testShellCmd) {
        String paramterFile = null;
        //获取参数列表,待调优系统为本系统支持的时候，否则为null
        SystemSut systemSut = systemSutMapper.selectByNameAndVersion(sutName,sutVersion);
        if (systemSut!=null){
            paramterFile = systemSut.getParameterFile();
        }
        UserSut userSut = UserSut.builder()
                .fkUserId(userId)
                .sutName(sutName)
                .sutVersion(sutVersion)
                .installPath(installPath)
                .parameterFile(paramterFile)
                .testShellCmd(testShellCmd)
                .createTime(new Date())
                .build();

            int insert = userSutMapper.insert(userSut);
        if (insert>0){
            log.info("成功插入一条userSut记录:{}",userSut.getId());
            return userSut;
        }else {
            log.error("插入一条userSut记录失败:{}", userSut.getId());
        }
        return null;
    }

    @Override
    public boolean testSut(boolean custom, String hardwareIp, String hardUsername, String hardPwd, String sutName, String sutVersion, String sutInstallPath, String testCmd) {
        //假定用户传的cmd是以;分隔的
        //custom=true时，testCmd必须不为空
        //custom=false时，testCmd必须为空（暂定）
        //  - 暂定默认使用类似"(ps -ef|grep mysql|grep -v grep|wc -l)>0 && echo 'true'"
        String cmd = null;
        String result = null;
        if (custom){
//            String testShellFile = sutTestShellDir + userId + "_"+sutName+"_"+sutVersion+"_testsut.sh";
//            cmd = FileUtils.readFileContentLineFeed(testShellFile);
            cmd = testCmd;
        }else {
            SystemSut systemSut = systemSutMapper.selectByNameAndVersion(sutName,sutVersion);
            if(systemSut!=null){
                cmd = systemSut.getTestShellCmd();
                if (cmd.contains("$sutInstallPath$")){
                    cmd.replace("$sutInstallPath$",sutInstallPath);
                }
            }
        }
        if (StringUtils.hasText(cmd)){
            result = SshUtils.executeReturnSuccess(hardwareIp,hardUsername,hardPwd,cmd);
            System.out.println(result);
        }
        //不同软件怎么样才算测试成功需要，对于内置情况需要添加成功条件字段吗，对于非内置是直接返回给用户返回信息还是让用户输入成功条件进行判断？
        //"test $(ps -ef|grep mysql.sock|grep -v grep|wc -l) -gt 0 && echo 'true' || echo 'false'"来进行成功判断
        System.out.println(result.replace("\n",""));
        if (cmd.contains("ps -ef") && result.replace("\n","").equals("true")){
            return true;
        }
        return false;
    }

    @Override
    public List<SystemSutVo> getSystemSutList() {
        List<SystemSutVo> systemSutList = new ArrayList<>();
        List<String> sutNameList = systemSutMapper.selectName();
        for (String sutName:sutNameList){
            List<String> versionList = systemSutMapper.selectVersionByName(sutName);
            SystemSutVo systemSutVo = new SystemSutVo();
            systemSutVo.setSutName(sutName);
            systemSutVo.setSutVersion(versionList);
            systemSutList.add(systemSutVo);
        }
        return systemSutList;
    }

    @Override
    public SystemSut addSystemSut(String sutName, String sutVersion, MultipartFile parameterFile, String testShellCmd,String workloads,MultipartFile logo){
        boolean success=true;
        //如果Logo为null，交个前端处理（前端使用默认Logo）
        String path="";
        if(logo!=null){
            String logoPath1=this.logoPath+"admin";
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemSut_"+UUID.randomUUID() +"."+strs[strs.length-1];
            success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }

        boolean uploadSuccess = FileUtils.uploadFile(parameterFile,System.getProperty("user.dir") + adminParameterFilePath);
        if (uploadSuccess&&success){
            String paramFilePath = adminParameterFilePath+File.separator+parameterFile.getOriginalFilename();
            SystemSut systemSut= new SystemSut();
            systemSut.setSutName(sutName);
            systemSut.setSutVersion(sutVersion);
            systemSut.setParameterFile(paramFilePath.replace("\\",File.separator).replace("/",File.separator));
            systemSut.setTestShellCmd(testShellCmd);
            systemSut.setWorkloads(workloads);
            systemSut.setLogoPath(path.replace("\\",File.separator).replace("/",File.separator));
            int insert=systemSutMapper.insert(systemSut);
            if (insert>0){
                log.info("成功插入一条systemSut记录:{}",systemSut.getSutName());
                return systemSut;
            }else {
                log.error("插入一条systemSut记录失败:{}", systemSut.getSutName());
            }
            return null;
        }else {
            log.info("上传参数文件时失败");
            return null;
        }
    }

    @Override
    public SystemSut updateSystemSut(Long systemSutId, String sutName, String sutVersion, MultipartFile parameterFile, String testShellCmd, MultipartFile logo,String workloads){
        SystemSut systemSut=systemSutMapper.selectByPrimaryKey(systemSutId);
        boolean success=true;
        String path=null;
        if(logo!=null){
            //删除之前的logo图片
            if (!systemSut.getLogoPath().equals("")){
                File file=new File(systemSut.getLogoPath());
                file.delete();
            }
            String logoPath1=this.logoPath+"admin";
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemSut_"+UUID.randomUUID() +"."+strs[strs.length-1];
            success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }
        boolean uploadSuccess=true;
        String paramFilePath=null;
        if(parameterFile!=null){
            uploadSuccess = FileUtils.uploadFile(parameterFile,System.getProperty("user.dir") + adminParameterFilePath);
            paramFilePath = adminParameterFilePath+File.separator+parameterFile.getOriginalFilename();
        }
        if (uploadSuccess&&success){
            SystemSut systemSut1=new SystemSut();
                    systemSut1.setId(systemSutId);
                    systemSut1.setSutName(sutName);
                    systemSut1.setSutVersion(sutVersion);
                    systemSut1.setParameterFile(paramFilePath);
                    systemSut1.setTestShellCmd(testShellCmd);
                    systemSut1.setWorkloads(workloads);
                    systemSut1.setLogoPath(path);
            int update=systemSutMapper.updateByPrimaryKeySelective(systemSut1);
            if(update>0){
                log.info("成功更新一条systemSut记录:{}",systemSut.getId());
                return systemSutMapper.selectByPrimaryKey(systemSutId);
            }else{
                log.info("更新一条systemSut记录失败:{}",systemSut.getId());
            }
            return null;
        }else {
            log.info("上传参数文件时失败");
            return null;
        }
    }

    @Override
    public int deleteSystemSut(Long systemSutId){
        //删除对应的参数文件和logo文件
        SystemSut systemSut=systemSutMapper.selectByPrimaryKey(systemSutId);
        String path=systemSut.getLogoPath();
        if(!path.equals("")){
            File file = new File(path);
            file.delete();
        }
        String parameterPath=systemSut.getParameterFile();
        File file = new File(System.getProperty("user.dir")+parameterPath);
        file.delete();

        int delete=systemSutMapper.deleteByPrimaryKey(systemSutId);
        return delete;
    }

    @Override
    public List<ParameterVo> getSystemEnvParamsDefault(Long userId,Long systemEnvId) throws InstanceNotFoundException, ServerException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        UserSut userSut = userSutMapper.selectByPrimaryKey(systemEnv.getFkSutId());
        String parameterFilePath = System.getProperty("user.dir") + userSut.getParameterFile();
        if (parameterFilePath!=null){
            return XlsUtils.getParamsFromXls(parameterFilePath);
        }
        return null;
    }

    @Override
    public List<ParameterVo> getSystemEnvParams(Long userId, Long systemEnvId) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        //待修改
        String parameters = systemEnv.getParameters();
        if (parameters != null){
            return JsonUtils.parameters2ParameterVoList(parameters);
        }
        return null;
    }

    @Override
    public int saveSystemEnvParams(Long userId, Long systemEnvId, String params) throws InstanceNotFoundException {
        //思路1(丢弃，字段变动了)
        //1.解析params json串转为list<ParameterVo>
        //2.list<ParameterVo> 存入到 xls文件中
        //3.将xls文件的路径保存到环境表中
        //思路2
        //用户确定参数后直接存储json，不通过文件
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        try {
            JsonUtils.parameters2ParameterVoList(params);
        }catch (Exception e){
            log.error("输入不符合参数列表格式");
            e.printStackTrace();
            throw new ServerErrorException("输入不符合参数列表格式");
        }
        systemEnv.setParameters(params);
        systemEnv.setModifyTime(new Date());
        return systemEnvMapper.updateByPrimaryKey(systemEnv);
    }

    @Transactional
    @Override
    public int uploadSystemEnvParamFile(Long userId, Long systemEnvId, MultipartFile paramFile) throws InstanceNotFoundException, ServerException {
        // TODO: 2020/12/31
        //  1. 文件上传后，需要将文件路径保存到该systemEnv关联的userSut表的paramFilePath字段（该字段会保存系统默认或用户上传的参数文件路径）
        if (paramFile.isEmpty()){
            throw new InstanceNotFoundException("上传失败，请选择文件");
        }
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        boolean uploadSuccess = FileUtils.uploadFile(paramFile,System.getProperty("user.dir") + adminParameterFilePath);
        if (uploadSuccess){
            String paramFilePath = adminParameterFilePath+File.separator+paramFile.getOriginalFilename();
            UserSut userSut = userSutMapper.selectByPrimaryKey(systemEnv.getFkSutId());
            //流程上，这里的userSut一定不为null
            userSut.setParameterFile(paramFilePath);
            userSut.setModifyTime(new Date());
            return userSutMapper.updateByPrimaryKey(userSut);
        }else {
            throw new ServerException("上传失败");
        }
    }

    @Override
    public void deleteUserSut(Long sutId) {
        userSutMapper.deleteByPrimaryKey(sutId);
    }

    @Override
    public void updateUserSut(Long sutId, String sutName, String sutVersion, String sutInstallPath, String sutTestCmd) {
        UserSut userSut = userSutMapper.selectByPrimaryKey(sutId);
        String paramterFile = null;
        //获取参数列表,待调优系统为本系统支持的时候，否则为null
        SystemSut systemSut = systemSutMapper.selectByNameAndVersion(sutName,sutVersion);
        if (systemSut!=null){
            paramterFile = systemSut.getParameterFile();
        }
        userSut.setSutName(sutName);
        userSut.setSutVersion(sutVersion);
        userSut.setInstallPath(sutInstallPath);
        userSut.setTestShellCmd(sutTestCmd);
        userSut.setParameterFile(paramterFile);
        userSut.setModifyTime(new Date());

        userSutMapper.updateByPrimaryKeySelective(userSut);
    }

    @Override
    public List<SystemSut> getAllSystemSut() {
        List<SystemSut> systemSuts= systemSutMapper.selectAll();
        for (SystemSut systemSut : systemSuts) {
            if(!systemSut.getLogoPath().equals("")){
                systemSut.setLogoPath(imageMap+systemSut.getLogoPath());
            }
        }
        return systemSuts;
    }

    @Override
    public List<ParameterVo> getParameterListDefault(String sutName,String sutVersion)throws Exception{
        SystemSut systemSut=systemSutMapper.selectByNameAndVersion(sutName,sutVersion);
        String parameterFilePath=System.getProperty("user.dir")+systemSut.getParameterFile();
        if (parameterFilePath!=null){
            return XlsUtils.getParamsFromXls(parameterFilePath);
        }
        return null;
    }
}
