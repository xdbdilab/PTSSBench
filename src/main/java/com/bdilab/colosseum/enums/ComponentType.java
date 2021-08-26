package com.bdilab.colosseum.enums;

/**
 * @ClassName ComponentType 组件类型枚举类
 * @Author wx
 * @Date 2021/3/1 0001 16:20
 **/
public enum  ComponentType {

    FEATURE_SELECT(0,"特征选择"),
    SAMPLING(1,"采样"),
    PERFORMANCE_PREDICTION_MODEL_CONSTRUCTION(2,"预测模型构建"),
    OPTIMAL_PARAMETER_SEARCH(3,"最优参数搜索");

    private Integer type;
    private String message;

    ComponentType(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
