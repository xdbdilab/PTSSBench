package com.bdilab.colosseum.exception;

import com.bdilab.colosseum.response.HttpCode;

/**
 * 自定义异常类
 * @author liran
 */
public class BusinessException extends RuntimeException{
    private final int messageCode;
    private final String responseMessage;
    private final String causeMessage;

    public BusinessException(int messageCode,String responseMessage, String causeMessage){
        super(causeMessage);
        this.messageCode = messageCode;
        this.responseMessage = responseMessage;
        this.causeMessage = causeMessage;
    }

    public BusinessException(HttpCode httpCode, String causeMessage){
        this(httpCode.getCode(),httpCode.getMessage(),causeMessage);
    }

    public BusinessException(int messageCode,String responseMessage, String causeMessage, Throwable cause){
        this(messageCode,responseMessage,causeMessage);
        this.initCause(cause);
    }

    public BusinessException(HttpCode httpCode, String causeMessage, Throwable cause){
        this(httpCode.getCode(),httpCode.getMessage(),causeMessage,cause);
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getCauseMessage() {
        return causeMessage;
    }
}
