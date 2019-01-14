package com.jit.skiad.jwtsecurity.handler;

import com.alibaba.fastjson.JSON;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理AuthenticationException异常，即：未登录状态下访问受保护资源
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(JSON.toJSONString(new ObjectRestResponse<Void>(ResultCode.PERMISSION_NO_ACCESS)));
    }
}
