package com.bdilab.colosseum.exception.handler;

import com.bdilab.colosseum.exception.BusinessException;
import com.bdilab.colosseum.response.ResponseResult;
import com.bdilab.colosseum.utils.ExceptionStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类，业务中抛出的异常都可以在这里统一捕获处理
 * @author liran
 */
@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseResult handleException(BusinessException e){
        logger.error("[BusinessException] "+e.getCauseMessage(),e);
        return new ResponseResult(e.getMessageCode(), e.getResponseMessage(), false);
    }

}
