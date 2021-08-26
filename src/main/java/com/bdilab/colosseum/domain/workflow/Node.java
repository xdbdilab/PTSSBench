package com.bdilab.colosseum.domain.workflow;

import java.util.List;
import java.util.Map;

/**
 * @Author duyeye
 * @Date 2020/12/28 0028 16:54
 */
public class Node {
    //前端生成的随机Id
    private String Id;

    //数据库中的id字段
    private String componentId;

    private String componentName;

    private String input;

    private String output;

    //Node的Id组成的List
    private List<String> priorNode;

    //Node的Id组成的List
    private List<String> rearNode;

    private Map<String, Object> componentParam;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<String> getPriorNode() {
        return priorNode;
    }

    public void setPriorNode(List<String> priorNode) {
        this.priorNode = priorNode;
    }

    public List<String> getRearNode() {
        return rearNode;
    }

    public void setRearNode(List<String> rearNode) {
        this.rearNode = rearNode;
    }

    public Map<String, Object> getComponentParam() {
        return componentParam;
    }

    public void setComponentParam(Map<String, Object> componentParam) {
        this.componentParam = componentParam;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
