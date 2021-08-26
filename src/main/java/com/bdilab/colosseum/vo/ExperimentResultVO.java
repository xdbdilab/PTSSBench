package com.bdilab.colosseum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName ExperimentResultVO 历史实验结果列表VO
 * @Author wx
 * @Date 2020/12/31 0031 17:42
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class ExperimentResultVO {
    private Long id;
    private Long experimentId;
    // 实验名
    private String experimentName;
    // 实验描述
    private String experimentDescription;
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
