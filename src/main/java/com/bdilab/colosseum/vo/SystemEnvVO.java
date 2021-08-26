package com.bdilab.colosseum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName SystemEnvVO
 * @Author wx
 * @Date 2021/1/4 0004 10:55
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemEnvVO {
    private Long id;
    // 硬件配置
    // 操作系统名
    private String osName;
    // 操作系统版本号
    private String version;
    // 服务器名
    private String hardwareName;
    // 服务器描述
    private String hardwareDescription;
    // 默认桥接模式0
    private Integer type;
    // 服务器ip
    private String ip;

    private String cpu;

    private String memory;

    private String disk;

    private String bandwidth;

    private String kernel;

    private String username;

    private String password;

    // SUT配置信息
    // SUT名
    private String sutName;
    // SUT版本号
    private String sutVersion;

    // WorkLoad配置信息
    // WorkLoad名（用户实际使用的SUT和workload一个是用户自行提供，填入到user_xxx表中；另外一个是选择系统提供的，然后系统将其存入到user_xxx中）
    private String workloadName;
    // WorkLoad版本号
    private String workloadVersion;

    // 创建时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    // 更新时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;
}
