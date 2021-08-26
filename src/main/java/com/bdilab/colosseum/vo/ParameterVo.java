package com.bdilab.colosseum.vo;

import lombok.Data;

import java.util.Set;

/**
 * @author SunRen
 * @version 1.0
 * @date 2020/12/20 10:53
 */
@Data
public class ParameterVo {
    private String name;
    private String type;
    private Object minValue;
    private Object maxValue;
    private String validValue;
    private String defaultValue;
}
