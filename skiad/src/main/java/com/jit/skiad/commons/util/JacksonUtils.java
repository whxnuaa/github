package com.jit.skiad.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JacksonUtils {
    public static ObjectMapper objectMapper;

    /**
     * 使用泛型方法,把json字符串转转为相应的javabean对象
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType){
        if (objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(jsonStr,valueType);
        } catch (IOException e) {
            log.error("jsonString to Object Type is ERROR :{}",e);
        }
        return null;
    }


    /**
     * 把javabean转换为json字符串
     */
    public static String toJson(Object object){
        if (objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Object to jsonString  is ERROR :{}",e);
        }
        return null;
    }
}
