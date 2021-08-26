package com.bdilab.colosseum.service.experiment.impl;

import com.alibaba.fastjson.JSON;
import com.bdilab.colosseum.bo.SoftwareLocationBO;
import com.bdilab.colosseum.service.experiment.Mysql80Service;
import com.bdilab.colosseum.utils.SshUtils;
import com.bdilab.colosseum.utils.XlsUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/21 15:11
 */
@Deprecated
@Service
public class Mysql80ServiceImpl implements Mysql80Service {

    private String execSysbenchMysql80(String ip, String username, String password, String sutLocation, String workloadLocation, String paramList, String performance){
        String mysqlResult = null;
        long startTime = System.currentTimeMillis();
        // 基准测试命令集合
        List<String> cmdList = new ArrayList<>();

        // 准备数据
        String OLTPPrepare = workloadLocation + "/bin/sysbench " + workloadLocation + "/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables=10 --table-size=100000 --threads=20 --time=100 --report-interval=10 --mysql-db=sys_test --mysql-socket=" + sutLocation + "/mysql.sock prepare";
        // 执行测试
        String OLTPRun = workloadLocation + "/bin/sysbench " + workloadLocation + "/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables=10 --table-size=100000 --threads=20 --time=100 --report-interval=10 --mysql-db=sys_test --mysql-socket=" + sutLocation + "/mysql.sock run > " + sutLocation + "/mysql-exp/mysysbench.log";
        // 清理数据
        String OLTPClean = workloadLocation + "/bin/sysbench " + workloadLocation + "/sysbench-master/share/sysbench/oltp_read_write.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password='123456' --tables=10 --table-size=100000 --threads=20 --time=100 --report-interval=10 --mysql-db=sys_test --mysql-socket=" + sutLocation + "/mysql/mysql.sock cleanup";
        // 本地编写mysql配置文件
        File my = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "myCnf");
        // 如果文件已存在，先删除再新建
        if (my.exists()) {
            my.delete();
            try {
                my.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String avg = null;

        try {
            System.out.println("配置文件覆盖完毕...");
            BufferedWriter bw = new BufferedWriter(new FileWriter(my, true));   //向mysql配置文件中写入配置数据
            System.out.println("开始覆盖配置文件...");
            bw.write("[mysqld]");
            bw.newLine();
            // 生成参数列表
            String[] param = paramList.split(",");
            for (int i = 0; i < param.length; i++) {
                bw.write(param[i]);
                bw.newLine();
            }
            bw.write("basedir=" + sutLocation);
            bw.newLine();
            bw.write("datadir=" + sutLocation + "/data");
            bw.newLine();
            bw.write("socket=" + sutLocation + "/mysql.sock");
            bw.newLine();
            bw.write("tmpdir=" + sutLocation +"/temp");
            bw.newLine();
            bw.write("user=mysql");
            bw.newLine();
            bw.write("symbolic-links=0");
            bw.newLine();
            bw.write("skip-grant-tables");
            bw.newLine();
            bw.write("default_authentication_plugin=mysql_native_password");
            bw.newLine();
            bw.write("binlog_expire_logs_seconds=600");
            bw.newLine();
            bw.write("[mysqld_safe]");
            bw.newLine();
            bw.write("log-error=/var/log/mysqld.log");
            bw.newLine();
            bw.write("pid-file=" + sutLocation + "/data/mysqld.pid");
            bw.newLine();
            bw.write("[client]");
            bw.newLine();
            bw.write("default-character-set=utf8");
            bw.newLine();
            bw.write("socket=" + sutLocation + "/mysql.sock");
            bw.newLine();
            bw.write("[mysql]");
            bw.newLine();
            bw.write("socket=" + sutLocation + "/mysql.sock");
            bw.newLine();
            bw.close();

            // 将文件上传到远程服务器中
            SshUtils.upload(ip, username, password, my.getAbsolutePath(), "/etc");

            // 清空mysql配置文件
            String cleanConfigFile = "> /etc/my.cnf";
            cmdList.add(cleanConfigFile);

            // 往配置文件中追加文件内容
            String appendConfigFile = "cat /etc/myCnf >> /etc/my.cnf";
            cmdList.add(appendConfigFile);

            System.out.println("开始测试...");

            // 重启mysql
            String restartCmd = "service mysql restart";
            cmdList.add(restartCmd);

            cmdList.add(OLTPPrepare);
            cmdList.add(OLTPRun);

            for (String cmd : cmdList) {
                SshUtils.executeReturnSuccess(ip, username, password, cmd);
            }

            // 读取测试结果
            // 根据用户传递的performance对应在mysql-performance.xls中找到对应的内容
            String performanceActual = XlsUtils.readExcel(System.getProperty("user.dir") + "/file/sys/performanceFile/mysql-performance.xls" ,performance,1).replace(".0","");
            System.out.println("工作负载中性能指标名performanceActual: " + performanceActual);
            String resultCmd = "grep " + performanceActual + ": " + sutLocation + "/mysql-exp/mysysbench.log";
            avg = SshUtils.executeReturnSuccess(ip, username, password, resultCmd).replaceAll(" ", "").split(":")[1];
            System.out.println("工作负载基准测试结果:" + avg);
            if (avg.contains("/")) {
                mysqlResult = avg.replace(avg.substring(avg.lastIndexOf("/"),avg.length()),"");
                System.out.println("性能指标值：" + mysqlResult);
            }
            // 对于特殊的几个性能指标特殊处理
            if ("transactions_per_sec".equals(performance) || "queries_per_sec".equals(performance)
            || "ignored_errors_per_sec".equals(performance) || "reconnects_per_sec".equals(performance)) {
                // subString左闭右开
                mysqlResult = avg.substring(avg.indexOf("(")+1,avg.indexOf("per"));
                System.out.println("性能指标值：" + mysqlResult);
            }
            if ("transactions".equals(performance) || "queries".equals(performance)
            || "ignored_errors".equals(performance) || "reconnects".equals(performance)) {
                mysqlResult = avg.substring(0,avg.indexOf("("));
                System.out.println("性能指标值：" + mysqlResult);
            }
            // 清除测试数据
            SshUtils.executeReturnSuccess(ip, username, password, OLTPClean);

        } catch (IOException |  ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时:"+(endTime-startTime));
        System.out.println("done");
        if (!isNumber(mysqlResult)){
            return "error";
        }
        return mysqlResult;
    }


    public static boolean isNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    @Override
    public String execSysbenchMysql80(String softwareLocationBOStr, String paramList, String performance) {
        SoftwareLocationBO softwareLocationBO = JSON.parseObject(softwareLocationBOStr,SoftwareLocationBO.class);
        return execSysbenchMysql80(softwareLocationBO.getIp(),softwareLocationBO.getUsername(),softwareLocationBO.getPassword(),softwareLocationBO.getSutPath(),softwareLocationBO.getWorkLoadPath(),paramList,performance);
    }


//    public static void main(String[] args) throws Exception {
//        String paramList = "innodb_buffer_pool_size=3,max_heap_table_size=5,join_buffer_size=7";
//        String[] param = paramList.split(",");
//        // 测试生成参数
//        for (int i = 0; i < param.length; i++) {
//            System.out.println(param[i]);
//        }
//        // 测试切分字符串
//        String avg = "99.9982/0.02";
//        System.out.println(avg.replace(avg.substring(avg.lastIndexOf("/"),avg.length()),""));
//        System.out.println(isNumber("100.0067"));
//        // 测试读取excel映射文件读取实际performance名称
//        String performance = XlsUtils.readExcel(System.getProperty("user.dir") + "/file/sys/performanceFile/mysql-performance.xls" ,"queries_performed_read").replace(".0","");
//        System.out.println("performance: " + performance);
//        // 测试ssh获取服务器上的性能指标值
//        String resultCmd = "grep " + performance + ": " + "/home/zhang/mysql" + "/mysql-exp/mysysbench.log";
//        String avg = SshUtils.executeReturnSuccess("47.105.185.177", "root", "bdilab@1308", resultCmd).replace(" ","").split(":")[1];
//        // 对于特殊的几个性能指标特殊处理
//        if ("transactions_per_sec".equals(performance) || "queries_per_sec".equals(performance)
//                || "ignored_errors_per_sec".equals(performance) || "reconnects_per_sec".equals(performance)) {
//            avg = avg.substring(avg.indexOf("(")+1,avg.indexOf("per"));
//        }
//        if ("transactions".equals(performance) || "queries".equals(performance)
//                || "ignored_errors".equals(performance) || "reconnects".equals(performance)) {
//            System.out.println(avg);
//            avg = avg.substring(0,avg.indexOf("("));
//        }
//        System.out.println(avg);
//    }
}
