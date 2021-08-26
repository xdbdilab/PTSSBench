package com.bdilab.colosseum.response;

/**
 * 定义返回数据格式
 */
public class ResponseResult {
    /**
     * 元数据
     */
    private MetaData meta;

    /**
     * 返回数据
     */
    private Object data;

    public ResponseResult(HttpCode httpCode, Object data) {
        this.meta = new MetaData(httpCode.getCode(), httpCode.getMessage(), httpCode.isSuccess());
        this.data=data;
    }

    public ResponseResult(MetaData meta, Object data) {
        this.meta = meta;
        this.data = data;
    }

    public ResponseResult(int code, String message, boolean success) {
        this.meta = new MetaData(code, message, success);
    }

    public ResponseResult(int code, String message, boolean success, Object data) {
        this.meta = new MetaData(code, message, success);
        this.data=data;
    }

    public MetaData getMeta() {
        return meta;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
