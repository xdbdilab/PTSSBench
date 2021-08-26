package com.bdilab.colosseum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ExperimentResultDetailVO
 * @Author wx
 * @Date 2020/12/31 0031 17:46
 **/
@Data
@Deprecated
public class ExperimentResultDetailVO {

    private Long id;
    private Long experimentId;
    // 实验配置详情
    private ExperimentDetailVO experiment;
    // 运行状态0-正在运行，1-已完成，2-运行失败（前端根据status区别展示，对于1状态的可以查看详情）
    private Integer status;
    // 运行结果
    private String result;
    // 运行开始时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;
    // 运行结束时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;
}
