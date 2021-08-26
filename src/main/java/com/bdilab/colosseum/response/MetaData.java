package com.bdilab.colosseum.response;

/**
 * 元数据
 */
public class MetaData {

    /**
     * 响应码
     */
    private int code;
    /**
     * 消息
     */
    private String message;
    /**
     * 响应状态
     */
    private boolean success;

    public MetaData(int code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
