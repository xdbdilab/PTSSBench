package com.bdilab.colosseum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ExperimentVO 实验列表展示VO
 * @Author wx
 * @Date 2020/12/31 0031 17:27
 **/
@Builder
@Data
public class ExperimentVO {
    // 实验ID
    private Long id;
    // 实验名
    private String experimentName;
    // 实验描述
    private String description;
    // 用户ID
    private Long userId;
    // 创建者名
    private String creator;
    //logo path
    private String logoPath;
    // 创建时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    // 更新时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifyTime;

}
