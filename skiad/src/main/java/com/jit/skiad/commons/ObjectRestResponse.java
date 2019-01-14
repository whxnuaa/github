package com.jit.skiad.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求响应类
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRestResponse<T> {
    private String code;

    private String message;

    private T data;

    public ObjectRestResponse(ResultCode status, T data){
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }

    public ObjectRestResponse(ResultCode status){
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = null;
    }
}
