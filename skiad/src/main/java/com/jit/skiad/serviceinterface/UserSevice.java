package com.jit.skiad.serviceinterface;

import com.github.pagehelper.PageInfo;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.dto.ResetPassword;
import com.jit.skiad.dto.UserAddDTO;
import com.jit.skiad.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserSevice {
    ObjectRestResponse<PageInfo<UserDTO>> getListByPage(Integer pageNum, Integer pageSize);
    ObjectRestResponse<List<UserDTO>> getList();

    ObjectRestResponse<UserDTO> getOneUserByAdmin(Integer userId);
    ObjectRestResponse<UserDTO> getOneUserByUser();

    ObjectRestResponse<UserDTO> resetPassword(HttpServletRequest request, ResetPassword resetPassword);

    ObjectRestResponse<Void> changePassword(ResetPassword changePassword);

    ObjectRestResponse<UserDTO> addUser(UserAddDTO userAddDTO);

    ObjectRestResponse<UserDTO> resetInfo(UserDO userDO);

    ObjectRestResponse<Boolean> deleteUserInfo(Integer userId);

    ObjectRestResponse<Boolean> deleteUserInfoBatch(String userIds);

    ObjectRestResponse<Boolean> permitInfo(Integer userId,Integer gwId);

    ObjectRestResponse<Void> initPassword(String username);

    ObjectRestResponse<List<UserDTO>> getUserPermissionByGwId(Integer gwId);
}
