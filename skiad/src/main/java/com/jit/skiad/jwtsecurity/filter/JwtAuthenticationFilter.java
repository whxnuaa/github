package com.jit.skiad.jwtsecurity.filter;

import com.alibaba.fastjson.JSON;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.jwtsecurity.bean.JwtProperty;
import com.jit.skiad.jwtsecurity.util.JwtUtil;
import com.jit.skiad.serviceimpl.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * token 过滤器
 * create by zm
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtProperty jwtProperty;
    private UserDetailsService userDetailsService;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_HEAD="Bearer ";

    public JwtAuthenticationFilter(JwtProperty jwtProperty, UserDetailsService userDetailsService) {
        this.jwtProperty = jwtProperty;
        this.userDetailsService = userDetailsService;
//        log.info("---------{}---------------",jwtProperty.toString());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json");
        String authorization = httpServletRequest.getHeader(TOKEN_HEADER);
        List<String> pass =  Arrays.asList("/auth/signup","/auth/login","/auth/logout");

//        String requestPath = httpServletRequest.getServletPath();
//        if (StringUtils.isEmpty(authorization) && !pass.contains(requestPath)) { // 未提供Token
//            httpServletResponse.getWriter().write(JSON.toJSONString(new ObjectRestResponse<Void>(ResultCode.TOKEN_NOT_PROVID)));
//            return;
//        }
        if (!StringUtils.isEmpty(authorization) && !authorization.startsWith(TOKEN_HEAD)) { // Token格式错误
            httpServletResponse.getWriter().write(JSON.toJSONString(new ObjectRestResponse<Void>(ResultCode.TOKEN_FORMAT_ERROR)));
            return;
        }
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith(TOKEN_HEAD)){
            Claims claims = JwtUtil.parseToken(authorization, jwtProperty.getBase64Security());

//            if (null == claims) { // Token不可解码
//                httpServletResponse.getWriter().write(JSON.toJSONString(new ObjectRestResponse<Void>(ResultCode.TOKEN_FORMAT_ERROR)));
//                return;
//            }
//            if (claims.getExpiration().getTime() < new Date().getTime()) { // Token超时
//                httpServletResponse.getWriter().write(JSON.toJSONString(new ObjectRestResponse<Void>(ResultCode.TOKEN_EXPIRED)));
//                return;
//            }
            String username = JwtUtil.getUsernameFromToken(authorization, jwtProperty);
            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (JwtUtil.validateToken(authorization, userDetails, jwtProperty)){
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
