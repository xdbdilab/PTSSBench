package com.bdilab.colosseum.vo;

import com.bdilab.colosseum.domain.workflow.Parameter;

import java.util.List;

/**
 * @Author duyeye
 * @Date 2020/12/29 0029 15:41
 */
public class ComponentVO {
    private Long id;

    private Byte type;

    private String componentName;

    private String description;

    private List<Parameter> parameters;

    private String logo_path;

    public String getLogo_path() {
        return logo_path;
    }

    public void setLogo_path(String logo_path) {
        this.logo_path = logo_path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
