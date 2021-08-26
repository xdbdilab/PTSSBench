package com.bdilab.colosseum.service.impl;


import com.bdilab.colosseum.domain.SystemEnv;
import com.bdilab.colosseum.domain.SystemSut;
import com.bdilab.colosseum.domain.SystemWorkload;
import com.bdilab.colosseum.domain.UserWorkload;
import com.bdilab.colosseum.mapper.SystemEnvMapper;
import com.bdilab.colosseum.mapper.SystemWorkloadMapper;
import com.bdilab.colosseum.mapper.UserWorkloadMapper;
import com.bdilab.colosseum.service.WorkloadService;
import com.bdilab.colosseum.utils.FileUtils;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import com.bdilab.colosseum.vo.ParameterVo;
import com.bdilab.colosseum.vo.PerformanceVo;
import com.bdilab.colosseum.vo.SystemWorkloadVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 20:40
 */
@Service
public class WorkloadServiceImpl implements WorkloadService {
    private static final Logger log = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    @Resource
    SystemWorkloadMapper systemWorkloadMapper;
    @Resource
    UserWorkloadMapper userWorkloadMapper;
    @Resource
    SystemEnvMapper systemEnvMapper;

    @Value("${logo.path}")
    private String logoPath;

    @Value("${image.map}")
    private String imageMap;

    private String adminParameterFilePath = "/file/sys/parameterFile";
    private String adminPerformanceFilePath = "/file/sys/performanceFile";

    @Override
    public UserWorkload addUserWorkload(Long userId, String workloadName, String workloadVersion, String installPath, String testShellCmd) {
        String performance = null;
        //获取参数列表
        SystemWorkload systemWorkload = systemWorkloadMapper.selectByNameAndVersion(workloadName,workloadVersion);
        if (systemWorkload!=null){
            performance = systemWorkload.getPerformance();
        }
        UserWorkload userWorkload = UserWorkload.builder()
                .fkUserId(userId)
                .workloadName(workloadName)
                .workloadVersion(workloadVersion)
                .installPath(installPath)
                .performance(performance)
                .testShellCmd(testShellCmd)
                .createTime(new Date())
                .execParams("")
                .build();

        int insert = userWorkloadMapper.insert(userWorkload);
        if (insert>0){
            log.info("成功插入一条userWorkload记录:{}",userWorkload.getId());
            return userWorkload;
        }else {
            log.error("插入一条userWorkload记录失败:{}",userWorkload.getId());
        }
        return null;
    }

    @Override
    public Map<String,Object> testWorkload(String hardwareIp, String hardUsername, String hardPwd, String workloadName, String workloadVersion, String workloadInstallPath, String testCmd) throws InstanceNotFoundException {
        //先根据名称和版本判断是否为未支持的工作负载，未支持则testCmd必须不为空，支持则根据testCmd是否有值返回不同的信息
        //对于是否为未支持的工作负载主要由前端判断。
        Map<String,Object> result = new HashMap<>();
//        SystemWorkload systemWorkload = systemWorkloadMapper.selectByNameAndVersion(workloadName,workloadVersion);
//        if (systemWorkload==null && StringUtils.isEmpty(testCmd)){
//            result.put("success",false);
//            result.put("msg","需要输入测试命令");
//            return result;
//        }
        //需要用户传的cmd是以;分隔的
        String cmd = null;
        String testResult = null;

        if (StringUtils.isEmpty(testCmd)){
            SystemWorkload systemWorkload = systemWorkloadMapper.selectByNameAndVersion(workloadName,workloadVersion);
            if(systemWorkload==null){
                throw new InstanceNotFoundException("该工作负载未支持，测试shell命令不得为空");
            }
            cmd = systemWorkload.getTestShellCmd();
            if (cmd.contains("$workloadInstallPath$")){
                cmd = cmd.replace("$workloadInstallPath$",workloadInstallPath);
            }
            if (!StringUtils.isEmpty(cmd)){
                testResult = SshUtils.executeReturnSuccess(hardwareIp,hardUsername,hardPwd,cmd);
                System.out.println(testResult);
            }
            //不同软件怎么样才算测试成功需要，对于内置情况需要添加成功条件字段吗，对于非内置是直接返回给用户返回信息还是让用户输入成功条件进行判断？
            //方案1：
            //  - 用户给自定义的测试脚本，我们就返回脚本的结果，让用户自己判断。
            //  - 系统内部内置的测试脚本都写成最后打印true的形式。
            //      例如sysbench,使用
            //      "test $(sh $workloadInstallPath$/tests/test_run.sh | grep '0 failed'|wc -l) -gt 0 && echo 'true' || echo 'false'"命令
            //      上面的命令有时候执行会出错改成
            //      “cd $workloadInstallPath$/tests;test $(sh ./test_run.sh | grep '0 failed'|wc -l) -gt 0 && echo 'true' || echo 'false'”
            if (testResult.replace("\n","").equals("true")){
                result.put("testSuccess",true);
                return result;
            }
            result.put("testSuccess",false);
            return result;
        }else {
            cmd = testCmd;
            testResult = SshUtils.execute(hardwareIp,hardUsername,hardPwd,cmd);
            System.out.println(testResult);
            result.put("result",testResult.replace("\n",""));
            return result;
        }
    }

    @Override
    public SystemWorkload addSystemWorkload(Long userId, String workloadName, String workloadVersion, MultipartFile performance, String testShellCmd, String execParams,MultipartFile logo) {
        SystemWorkload select = systemWorkloadMapper.selectByNameAndVersion(workloadName, workloadVersion);
        if (select != null) {
            log.info("用户userId:{}插入workloadName:{}和workloadVersion:{}已存在", userId, workloadName, workloadVersion);
            return null;
        }
        boolean success=true;
        //如果Logo为null，交个前端处理（前端使用默认Logo）
        String path="";
        if(logo!=null){
            String logoPath1=this.logoPath+"admin";
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemWorkload_"+UUID.randomUUID() +"."+strs[strs.length-1];
            success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }

        boolean uploadSuccess = FileUtils.uploadFile(performance,System.getProperty("user.dir") + adminPerformanceFilePath);
        if (uploadSuccess&&success){
            String paramFilePath = adminPerformanceFilePath+File.separator+performance.getOriginalFilename();
            SystemWorkload systemWorkload = new SystemWorkload();
            systemWorkload.setWorkloadName(workloadName);
            systemWorkload.setWorkloadVersion(workloadVersion);
            systemWorkload.setPerformance(paramFilePath);
            systemWorkload.setTestShellCmd(testShellCmd);
            systemWorkload.setExecParams(execParams);
            systemWorkload.setLogoPath(path);
            int insert = systemWorkloadMapper.insert(systemWorkload);
            if (insert > 0) {
                log.info("用户userId:{}成功插入一条systemWorkload记录:{}", userId, systemWorkload.toString());
                return systemWorkload;
            } else {
                log.error("用户userId:{}插入一条systemWorkload记录失败:{}", userId, systemWorkload.toString());
            }
        }
        return null;
    }

    @Override
    public List<SystemWorkloadVo> getSystemWorkloadList() {
        List<SystemWorkloadVo> systemWorkloadList = new ArrayList<>();
        List<String> nameList = systemWorkloadMapper.selectName();
        for (String name:nameList){
            List<String> versionList = systemWorkloadMapper.selectVersionByName(name);
            SystemWorkloadVo systemWorkloadVo = new SystemWorkloadVo();
            systemWorkloadVo.setWorkloadName(name);
            systemWorkloadVo.setWorkloadVersion(versionList);
            systemWorkloadList.add(systemWorkloadVo);
        }
        return systemWorkloadList;
    }

    @Override
    public String getSystemEnvPerformanceDefault(Long userId, Long systemEnvId) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        UserWorkload userWorkload = userWorkloadMapper.selectByPrimaryKey(systemEnv.getFkWorkloadId());
        return userWorkload.getPerformance();

    }

    @Override
    public String getSystemEnvPerformance(Long userId, Long systemEnvId) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        //数据库中的performance是以字符串的形式保存的,"name1,name2"
        return systemEnv.getPerformance();
    }

    @Override
    public int saveSystemEnvPerformance(Long userId, Long systemEnvId, String performances) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        systemEnv.setPerformance(performances);
        systemEnv.setModifyTime(new Date());
        return systemEnvMapper.updateByPrimaryKey(systemEnv);
    }

    @Override
    public void deleteUserWorkload(Long workloadId) {
        userWorkloadMapper.deleteByPrimaryKey(workloadId);
    }

    @Override
    public void updateUserWorkload(Long workloadId, String workloadName, String workloadVersion, String workloadInstallPath, String workloadTestCmd) {
        UserWorkload userWorkload = userWorkloadMapper.selectByPrimaryKey(workloadId);
        String performance = null;
        //获取参数列表
        SystemWorkload systemWorkload = systemWorkloadMapper.selectByNameAndVersion(workloadName,workloadVersion);
        if (systemWorkload!=null){
            performance = systemWorkload.getPerformance();
        }
        userWorkload.setWorkloadName(workloadName);
        userWorkload.setWorkloadVersion(workloadVersion);
        userWorkload.setInstallPath(workloadInstallPath);
        userWorkload.setTestShellCmd(workloadTestCmd);
        userWorkload.setPerformance(performance);
        userWorkload.setModifyTime(new Date());

        userWorkloadMapper.updateByPrimaryKeySelective(userWorkload);
    }

    @Override
    public void deleteSystemWorkload(Long userId, Long workloadId) {
        //删除对应的参数文件和logo文件
        SystemWorkload systemWorkload=systemWorkloadMapper.selectByPrimaryKey(workloadId);
        String path=systemWorkload.getLogoPath();
        if(!path.equals("")){
            File file = new File(path);
            file.delete();
        }
        String performancePath=systemWorkload.getPerformance();
        File file = new File(System.getProperty("user.dir")+performancePath);
        file.delete();
        int delete = systemWorkloadMapper.deleteByPrimaryKey(workloadId);
        if (delete > 0) {
            log.info("用户userId:{}成功删除系统工作负载workloadId:{}", userId, workloadId);
        }
    }

    @Override
    public SystemWorkload updateSystemWorkload(Long userId, Long workloadId, String workloadName, String workloadVersion, MultipartFile performance, String testShellCmd,String execParams,MultipartFile logo) {
        SystemWorkload systemWorkload=systemWorkloadMapper.selectByPrimaryKey(workloadId);
        boolean success=true;
        String path=null;
        if(logo!=null){
            //删除之前的logo图片
            if (!systemWorkload.getLogoPath().equals("")){
                File file=new File(systemWorkload.getLogoPath());
                file.delete();
            }
            String logoPath1=this.logoPath+"admin";
            String[] strs=logo.getOriginalFilename().split("\\.");
            String fileName="systemWorkload_"+UUID.randomUUID() +"."+strs[strs.length-1];
            success=FileUtils.uploadFileRename(logo,logoPath1,fileName);
            path=logoPath1+File.separator+fileName;
        }

        boolean uploadSuccess=true;
        String performancePath=null;
        if(performance!=null){
            uploadSuccess = FileUtils.uploadFile(performance,System.getProperty("user.dir") + adminPerformanceFilePath);
            performancePath = adminPerformanceFilePath+File.separator+performance.getOriginalFilename();
        }
       if (uploadSuccess&&success){
            SystemWorkload systemWorkload1= new SystemWorkload();
            systemWorkload1.setId(workloadId);
            systemWorkload1.setWorkloadName(workloadName);
            systemWorkload1.setWorkloadVersion(workloadVersion);
            systemWorkload1.setPerformance(performancePath);
            systemWorkload1.setTestShellCmd(testShellCmd);
            systemWorkload1.setExecParams(execParams);
            systemWorkload1.setLogoPath(path);
            int update = systemWorkloadMapper.updateByPrimaryKeySelective(systemWorkload1);
            if (update > 0) {
                log.info("用户userId:{}成功更新系统工作负载workloadId:{}", userId, workloadId.toString());
                return systemWorkloadMapper.selectByPrimaryKey(workloadId);
            }
        }

        log.error("用户userId:{}更新系统工作负载workloadId:{}失败", userId, workloadId.toString());
        return null;
    }

    @Override
    public int setPerformanceDefault(Long userId, Long systemEnvId, String performances) throws InstanceNotFoundException {
        SystemEnv systemEnv = systemEnvMapper.selectByIdAndUserId(userId,systemEnvId);
        if (systemEnv == null){
            throw new InstanceNotFoundException("环境不存在");
        }
        UserWorkload userWorkload = userWorkloadMapper.selectByPrimaryKey(systemEnv.getFkWorkloadId());
        userWorkload.setPerformance(performances);
        userWorkload.setModifyTime(new Date());
        return userWorkloadMapper.updateByPrimaryKeySelective(userWorkload);
    }

    @Override
    public List<SystemWorkload> getAllSystemWorkload() {
        List<SystemWorkload> systemWorkloadList= systemWorkloadMapper.selectAll();
        for (SystemWorkload systemWorkload : systemWorkloadList) {
            if(!systemWorkload.getLogoPath().equals("")){
                systemWorkload.setLogoPath(imageMap+systemWorkload.getLogoPath());
            }
        }
        return systemWorkloadList;
    }

    @Override
    public List<SystemWorkloadVo> getWorkloads(String workloads){
        String[] workloadArgs=workloads.split(",");
        List<SystemWorkloadVo> systemWorkloadVos=new ArrayList<>();
        for (String workloadArg : workloadArgs) {
            List<String> versions=systemWorkloadMapper.selectVersionByName(workloadArg);
            SystemWorkloadVo systemWorkloadVo = new SystemWorkloadVo();
            systemWorkloadVo.setWorkloadName(workloadArg);
            systemWorkloadVo.setWorkloadVersion(versions);
            systemWorkloadVos.add(systemWorkloadVo);
        }
        return systemWorkloadVos;
    }

    //sysbench性能指标集合"queries_performed_read,queries_performed_write,queries_performed_other,queries_performed_total,transactions,transactions_per_sec,queries,queries_per_sec,ignored_errors,ignored_errors_per_sec,reconnects,reconnects_per_sec,throughput_events/s(eps),throughput_time_elapsed,throughput_total_number_od_events,latency_min(ms),latency_avg(ms),latency_max(ms),latency_95th_percentile(ms),latency_sum(ms),threads_fairness_events(avg/stddev),threads_fairness_execution_time(avg/stddev)"
    @Override
    public List<PerformanceVo> getPerformanceListDefault(String workloadName, String workloadVersion)throws Exception{
        SystemWorkload systemWorkload=systemWorkloadMapper.selectByNameAndVersion(workloadName,workloadVersion);

        String performanceFilePath=System.getProperty("user.dir")+systemWorkload.getPerformance();
        if (performanceFilePath!=null){
            return XlsUtils.getPerformanceFromXls(performanceFilePath);
        }
        return null;
    }
}
