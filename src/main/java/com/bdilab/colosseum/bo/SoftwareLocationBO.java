package com.bdilab.colosseum.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SoftwareLocationBO 软件安装路径BO
 * @Author wx
 * @Date 2021/1/8 0008 20:42
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareLocationBO {
    // 待调优系统安装路径
    String sutPath;

    // 工作负载安装路径
    String workLoadPath;

    // 实验服务器IP
    String ip;

    // 实验服务器登录名
    String username;

    // 实验服务器登录密码
    String password;
}
