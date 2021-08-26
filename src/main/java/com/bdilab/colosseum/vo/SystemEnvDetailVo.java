package com.bdilab.colosseum.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author SunRen
 * @version 1.0
 * @date 2021/1/8 20:10
 */
@Data
public class SystemEnvDetailVo {
    private String envName;

    private String envDesc;

    private Long fkUserId;

    private String parameters;

    private String performance;

    private String logoPath;

    private String hardwareName;

    private String hardwareDesc;

    private Byte hardType;

    private String ip;

    private String cpu;

    private String memory;

    private String disk;

    private String bandwidth;

    private String kernel;

    private String osName;

    private String osVersion;

    private String hardUsername;

    private String hardPassword;

    private String sutName;

    private String sutVersion;

    private String sutInstallPath;

    private String parameterFile;

    private String sutTestShellCmd;

    private String workloadName;

    private String workloadVersion;

    private String workloadInstallPath;

    private String workloadPerformance;

    private String workloadTestShellCmd;

}
