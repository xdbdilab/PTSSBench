package com.bdilab.colosseum.utils;

import ch.ethz.ssh2.*;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/15 14:02
 **/
public class SshUtils {
    private static final Logger log = LoggerFactory.getLogger(SshUtils.class);
    private static String  DEFAULTCHART="UTF-8";

    /**
     * 登录主机
     * @return
     *      登录成功返回true，否则返回false
     */
    public static boolean testConnect(String ip,
                                   String userName,
                                   String userPwd){
        boolean flag=false;
        Connection conn = new Connection(ip);
        try {
            conn.connect();//连接
            flag=conn.authenticateWithPassword(userName, userPwd);//认证
            if(flag){
                log.info("=========服务器连接成功========="+conn);
            }else {
                log.info("=========服务器认证未通过========="+conn);
            }
        } catch (IOException e) {
            log.error("=========服务器连接失败========="+e.getMessage());
            e.printStackTrace();
        }finally {
            conn.close();
            log.info("=========服务器连接关闭========="+conn);
        }
        return flag;
    }

    /**
     * 远程执行shll脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public static String execute(String ip, String username, String password,String cmd){
        String result="";
        Connection conn = new Connection(ip);
        Session session = null;
        try {
            conn.connect();
            boolean isConn = conn.authenticateWithPassword(username, password);
            if(isConn){
                session= conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                InputStream outStream = new StreamGobbler(session.getStdout());
                InputStream errStream = new StreamGobbler(session.getStderr());
                String output = IOUtils.toString(outStream, DEFAULTCHART);
                String err = IOUtils.toString(errStream, DEFAULTCHART);
                result= output;
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isEmpty(output)){
                    log.info("得到标准输出为空,链接conn:"+conn+",执行的命令："+cmd);
                    result=err;
                }else{
                    log.info("执行命令成功,链接conn:"+conn+",执行的命令："+cmd);
                }
            }
        } catch (IOException e) {
            log.error("执行命令失败,链接conn:"+conn+",执行的命令："+cmd+"  "+e.getMessage());
            e.printStackTrace();
        }finally {
            if( session != null) {
                session.close();
            }
            conn.close();
        }
        return result;
    }

    /**
     * 远程执行shll脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public static String executeReturnSuccess(String ip, String username, String password,String cmd){
        String result="";
        Connection conn = new Connection(ip);
        Session session = null;
        try {
            conn.connect();
            boolean isConn = conn.authenticateWithPassword(username, password);
            if(isConn){
                session= conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                InputStream outStream = new StreamGobbler(session.getStdout());
                String output = IOUtils.toString(outStream, DEFAULTCHART);
                result= output;
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isEmpty(output)){
                    log.info("得到标准输出为空,链接conn:"+conn+",执行的命令："+cmd);
                }else{
                    log.info("执行命令成功,链接conn:"+conn+",执行的命令："+cmd);
                }
            }
        } catch (IOException e) {
            log.error("执行命令失败,链接conn:"+conn+",执行的命令："+cmd+"  "+e.getMessage());
            e.printStackTrace();
        }finally {
            if( session != null) {
                session.close();
            }
            conn.close();
        }
        return result;
    }

    /**
     * 将本地文件上传到远程服务器中
     * @param ip
     * @param username
     * @param password
     * @param localFilePath
     * @param remoteDir 远程文件目录
     */
    public static void upload(String ip, String username, String password, String localFilePath, String remoteDir) {
        Connection conn = new Connection(ip);
        Session session = null;
        try {
            conn.connect();
            boolean isConn = conn.authenticateWithPassword(username, password);
            if(isConn){
                SCPClient client = new SCPClient(conn);
                // 将File转为MultipartFile对象
                File localFile = new File(localFilePath);
                FileInputStream fileInputStream = new FileInputStream(localFile);
                MultipartFile multipartFile = new MockMultipartFile(localFile.getName(), localFile.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
                byte[] b = multipartFile.getBytes();
                // 删掉远程的文件
                executeReturnSuccess(ip, username, password, "rm -f " + remoteDir + "/" + localFile.getName());

                // 判断远程服务器文件夹是否存在，若不存在会自动创建
                SCPOutputStream os = client.put(localFile.getName(), b.length, remoteDir, null);
                os.write(b,0,b.length);

                // 打开一个会话
                session= conn.openSession();
                // 远程执行linux命令 因为上传的文件没有读的文件 需要加上才能下载 （如果你上传的文件有）
                String cmd = "chmod +r "+ remoteDir + "/" + localFile.getName();
                System.out.println("linux命令=="+cmd);
                // 执行命令
                session.execCommand(cmd);
                os.flush();
                os.close();
                conn.close();
            }
        } catch (IOException e) {
            log.error("执行命令失败,链接conn:"+conn+", 上传文件失败 "+e.getMessage());
            e.printStackTrace();
        }finally {
            if( session != null) {
                session.close();
            }
            conn.close();
        }
    }


    /**
     * 获取SFTPv3Client
     * @param conn
     * @return
     * @throws IOException
     */
    public static SFTPv3Client getClient(Connection conn) throws IOException {
        SFTPv3Client client = new SFTPv3Client(conn);
        return client;
    }

        /**
         * 远程读取Linux上的文件并保存到本地文件
         * @param ip
         * @param username
         * @param password
         * @param filePath
         * @return
         */
    public static String downloadFile(String ip, String username, String password, String filePath, String performance) {
        Connection conn = new Connection(ip);
        Session session = null;
        try {
            conn.connect();
            boolean isConn = conn.authenticateWithPassword(username, password);
            if(isConn){
                // 打开一个会话
                session= conn.openSession();
//                // 获取文件大小
//                session.execCommand("du -b".concat(filePath));
//                InputStream sizeIn = new StreamGobbler(session.getStdout());
//                // 将字节流转换为字符流
//                InputStreamReader inputStreamReader = new InputStreamReader(sizeIn);
//                // 创建字符流缓冲区
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String line;
//                // 获取文件大小
//                int fileSize = 0;
//                while ((line = bufferedReader.readLine()) != null) {
//                    String[] fileArr = line.split("\t");
//                    fileSize = Integer.parseInt(fileArr[0]);
//                }
//                inputStreamReader.close();
                // 休眠防止网络延迟导致读取文件大小为0字节
//                Thread.sleep(2000);
                session.execCommand("cat".concat(filePath));
                InputStream inputStream = new StreamGobbler(session.getStdout());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(performance)) {
                        return line;
                    }
                }

            }
        } catch (IOException e) {
            log.error("执行命令失败,链接conn:"+conn+", 上传文件失败 "+e.getMessage());
            e.printStackTrace();
        }finally {
            if( session != null) {
                session.close();
            }
            conn.close();
        }
        return "";
    }
}
