package com.jit.skiad.exception;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ObjectRestResponse handleException(Exception exception) {
        exception.printStackTrace(); // 打印异常
        log.error("controller error {}",exception);
        return new ObjectRestResponse(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
    }
}
