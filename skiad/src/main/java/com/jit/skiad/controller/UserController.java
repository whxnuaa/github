package com.jit.skiad.controller;

import com.github.pagehelper.PageInfo;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.dto.ResetPassword;
import com.jit.skiad.dto.UserAddDTO;
import com.jit.skiad.dto.UserDTO;
import com.jit.skiad.serviceinterface.UserSevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "用户管理",description = "用户基本信息管理")
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserSevice userSevice;

    /**
     * 管理员分页获取全部用户
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return 用户列表
     */
    @ApiOperation(value = "管理员分页获取全部用户",notes = "管理员分页获取全部用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "起始页码", required = false, dataType = "int"),
            @ApiImplicitParam(name="pageSize",value = "每页数量",required = false,dataType = "int")
    })
    @GetMapping("admin/page")
    ObjectRestResponse<PageInfo<UserDTO>> getListByPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return userSevice.getListByPage(pageNum,pageSize);
    }


    /**
     * 管理员获取全部用户
     * @return 用户列表
     */
    @ApiOperation(value = "管理员不分页获取全部用户",notes = "管理员不分页获取全部用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String")
    })
    @GetMapping("admin/all")
    ObjectRestResponse<List<UserDTO>> getLists(){
        return userSevice.getList();
    }

    /**
     * 管理员获取某一用户的信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "管理员获取某一用户的信息",notes = "管理员获取某一用户的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "int")
    })
    @GetMapping("admin/one")
    ObjectRestResponse<UserDTO> getOneUserbyAdmin(@RequestParam("userId")Integer userId){
        return userSevice.getOneUserByAdmin(userId);
    }

    /**
     * 用户获取个人信息
     * @return
     */
    @ApiOperation(value = "用户获取个人信息",notes = "用户获取个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String")
    })
    @GetMapping("user/one")
    ObjectRestResponse<UserDTO> getOneUserbyUser(){
        return userSevice.getOneUserByUser();
    }

    /**
     * 重置密码
     * @param request HttpServlet 请求
     * @param resetPassword 密码对象
     * @return 带有token的用户信息
     */
    @ApiOperation(value = "重置密码",notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "resetPassword", value = "密码对对象", required = true, dataType = "ResetPassword")
    })
    @PutMapping("user/resetpassword")
    ObjectRestResponse<UserDTO> resetPassword(HttpServletRequest request, @RequestBody ResetPassword resetPassword){
        return userSevice.resetPassword(request,resetPassword);
    }
    /**
     * 修改用户密码
     * @param changePassword 密码对象
     * @return
     */
    @ApiOperation(value = "修改密码",notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "changePassword", value = "密码对对象", required = true, dataType = "ResetPassword")
    })
    @PutMapping("user/changepassword")
    ObjectRestResponse<Void> changePassword(@RequestBody ResetPassword changePassword){
        return userSevice.changePassword(changePassword);
    }

    /**
     * 新增用户信息
     * @param userDO
     * @return
     */
    @ApiOperation(value = "新增用户信息",notes = "管理员增加用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userDO", value = "用户信息", required = true, dataType = "UserAddDTO")
    })
    @PostMapping("admin/add")
    ObjectRestResponse<UserDTO> addUser(@RequestBody UserAddDTO userDO){
        return userSevice.addUser(userDO);
    }

    /**
     *更新用户信息
     * @param userDO
     * @return
     */
    @ApiOperation(value = "更新用户信息",notes = "管理员更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userDO", value = "用户信息", required = true, dataType = "UserDO")
    })
    @PutMapping("user/resetinfo")
    ObjectRestResponse<UserDTO> resetUserInfo( @RequestBody UserDO userDO){
        return userSevice.resetInfo(userDO);
    }

    /**
     * 管理员删除用户
     * @param userId
     * @return
     */
    @ApiOperation(value = "管理员删除用户",notes = "管理员删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "int")
    })
    @DeleteMapping("admin/{userId}")
    ObjectRestResponse<Boolean> deleteUserInfo(@PathVariable Integer userId){
        return userSevice.deleteUserInfo(userId);
    }

    /**
     * 管理员批量删除用户
     * @param userIds
     * @return
     */
    @ApiOperation(value = "管理员批量删除用户",notes = "管理员批量删除用户,用户id用“-”分割")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userIds", value = "用户id", required = true, dataType = "string")
    })
    @DeleteMapping("admin/ids")
    ObjectRestResponse<Boolean> deleteUserInfo(@RequestParam("userIds") String userIds){
        return userSevice.deleteUserInfoBatch(userIds);
    }



    @ApiOperation(value = "根据网关id获取权限列表",notes = "根据网关id获取权限列表,不包含管理员角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name="gwId",value = "网关id",required = true,dataType = "int")
    })
    @GetMapping("user/permission")
    ObjectRestResponse<List<UserDTO>> getUserPermissionByGwId(@RequestParam("gwId")Integer gwId){
        return userSevice.getUserPermissionByGwId(gwId);
    }

    /**
     * 分配用户权限
     * @param userId
     * @return
     */
    @ApiOperation(value = "管理员是否启用用户",notes = "管理员是否启用用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "int"),
            @ApiImplicitParam(name="gwId",value = "网关id",required = true,dataType = "int")
    })
    @PutMapping("admin/permit")
    ObjectRestResponse<Boolean> permitUserInfo(@RequestParam("userId") Integer userId,@RequestParam("gwId")Integer gwId){
        return userSevice.permitInfo(userId,gwId);
    }

    /**
     * 管理员初始化用户密码
     * @param username
     * @return
     */
    @ApiOperation(value = "管理员恢复用户初始密码",notes = "管理员恢复用户初始密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "username", value = "登录名", required = true, dataType = "String")
    })
    @PutMapping("admin/initpassword")
    ObjectRestResponse<Void> initPassword(@RequestParam("username") String username){
        return userSevice.initPassword(username);
    }




}
