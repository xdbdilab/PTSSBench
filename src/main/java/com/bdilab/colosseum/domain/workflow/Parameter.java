package com.bdilab.colosseum.domain.workflow;

import java.util.List;

/**
 * @Author duyeye
 * @Date 2020/12/29 0029 15:17
 */
public class Parameter {
    private String paramName;

    private Long fkComponentId;

    private String paramType;

    private Object defaultValue;

    private String paramDesc;

    private String[] enumValue;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Long getFkComponentId() {
        return fkComponentId;
    }

    public void setFkComponentId(Long fkComponentId) {
        this.fkComponentId = fkComponentId;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String[] getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String[] enumValue) {
        this.enumValue = enumValue;
    }
}
