package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.JwtUser;
import com.jit.skiad.dto.UserDTO;
import com.jit.skiad.serviceimpl.UserDetailServiceImpl;
import com.jit.skiad.serviceinterface.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "注册登录",description = "用户注册登录账户")
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserDetailServiceImpl userService;
    @Autowired
    private AuthService authService;

    /**
     * 注册账号
     * @param addedUser
     * @return
     */
    @PostMapping(value = "signup")
    @ApiOperation(value = "注册账号",notes = "用户名、手机号和密码注册")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "Authorization", value = "该参数值（value='bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "addedUser", value = "登录帐户和密码", required = true, dataType = "UserDTO")
    })
    public ObjectRestResponse<Void> register(@RequestBody UserDTO addedUser) {
        return authService.register(addedUser);
    }

    /**
     * 登录系统
     * @param
     * @return
     */
    @ApiOperation(value = "登录系统",notes = "用户名或手机号和密码登录")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "jwtUser", value = "登录帐户和密码", required = true, dataType = "JwtUser")
    })
    @PostMapping(value = "login")
    public ObjectRestResponse<UserDTO> createAuthenticationToken(@RequestBody JwtUser jwtUser){
        log.info("---------------------login 1 -------------");
        return authService.login(jwtUser.getUsername(),jwtUser.getPassword());
    }

    /**
     * 用户忘记密码
     * @param username 用户名
     * @return
     */
    @ApiOperation(value = "用户忘记密码",notes = "用户忘记密码,请求管理员重置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "登录名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "number",value = "工号",required = true,dataType = "String")
    })
    @PutMapping("forgetpw")
    public ObjectRestResponse<Void> forgetPassWord(@RequestParam("username") String username,@RequestParam("number") String number){
        return authService.forgetPassWord(username,number);
    }
}
