package com.bdilab.colosseum.response;

/**
 * 定义响应码格式
 */
public enum HttpCode {
    OK(200,"请求成功",true),
    BAD_REQUEST(400,"请求错误",false),
    UNAUTHORIZED(401,"请求未授权",false),
    FORBIDDEN(403,"请求被禁止",false),
    NOT_FOUND(404,"资源不存在",false),
    SERVER_ERROR(500,"内部服务器错误",false);

    private int code;
    private String message;
    private boolean success;

    HttpCode(int code, String message,boolean success) {
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
