package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.domain.GatewayDO;
import com.jit.skiad.domain.GwUserDO;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.domain.UserRoleDO;
import com.jit.skiad.dto.ResetPassword;
import com.jit.skiad.dto.UserAddDTO;
import com.jit.skiad.dto.UserDTO;
import com.jit.skiad.jwtsecurity.bean.JwtProperty;
import com.jit.skiad.jwtsecurity.util.CustomPasswordEncoder;
import com.jit.skiad.jwtsecurity.util.JwtUtil;
import com.jit.skiad.mapper.GatewayMapper;
import com.jit.skiad.mapper.GwUserMapper;
import com.jit.skiad.mapper.UserMapper;
import com.jit.skiad.mapper.UserRoleMapper;
import com.jit.skiad.serviceinterface.UserSevice;
import io.swagger.models.auth.In;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jit.skiad.jwtsecurity.util.JwtUtil.createToken;

@Service
public class UserSeviceImpl implements UserSevice {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private GwUserMapper gwUserMapper;

    @Autowired
    private JwtProperty jwtProperty;

    private final String TOKEN_HEADER = "Authorization";

    /**
     * 分页获取所有人员，不包括管理员
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ObjectRestResponse<PageInfo<UserDTO>> getListByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserDTO> returnUser = getAllUsers();
        return new ObjectRestResponse<>(ResultCode.SUCCESS, new PageInfo(returnUser));
    }

    /**
     * 不分页获取所有人员，不包括管理员
     *
     * @return
     */
    @Override
    public ObjectRestResponse<List<UserDTO>> getList() {
        List<UserDTO> returnUser = getAllUsers();
        return new ObjectRestResponse<List<UserDTO>>(ResultCode.SUCCESS, returnUser);
    }

    private List<UserDTO> getAllUsers() {
        List<UserDTO> users = userMapper.selectLists(null);
        List<UserDTO> returnUser = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserRoleDO userRoleDO = userRoleMapper.selectOne(UserRoleDO.of().setUserId(users.get(i).getId()));
            if (userRoleDO == null) {
                continue;
            } else {
                if (userRoleDO.getRoleId() != 1) {
                    UserDTO userDTO = UserDTO.of();
                    //不包含管理员
                    List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("user_id", users.get(i).getId()));
                    BeanUtils.copyProperties(users.get(i), userDTO);
                    userDTO.setGwIds(gwUserDOList);
                    returnUser.add(userDTO);
                }
            }
        }
        return returnUser;
    }

    /**
     * 获取某一网关下的所有用户
     *
     * @param gwId
     * @return
     */
    @Override
    public ObjectRestResponse<List<UserDTO>> getUserPermissionByGwId(Integer gwId) {
        List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("gw_id", gwId));
        List<UserDTO> userDTOList = new ArrayList<>();

        if (gwUserDOList.size() != 0) {
            for (int i = 0; i < gwUserDOList.size(); i++) {
                UserRoleDO userRoleDO = userRoleMapper.selectOne(UserRoleDO.of().setUserId(gwUserDOList.get(i).getUserId()));
                if (userRoleDO != null) {
                    if (userRoleDO.getRoleId() != 1) {//不包含管理员
                        UserDO userDO = userMapper.selectOne(UserDO.of().setId(gwUserDOList.get(i).getUserId()));
                        if (userDO != null) {
                            UserDTO userDTO = UserDTO.of();
                            BeanUtils.copyProperties(userDO, userDTO);
                            userDTO.setUseFlag(gwUserDOList.get(i).getUseFlag());
                            userDTOList.add(userDTO);
                        }

                    }
                }

            }
        }
        return new ObjectRestResponse<List<UserDTO>>(ResultCode.SUCCESS, userDTOList);
    }


    /**
     * 管理员获取某一个用户的基本信息
     *
     * @param userId
     * @return
     */
    @Override
    public ObjectRestResponse<UserDTO> getOneUserByAdmin(Integer userId) {
        UserDO userDO = userMapper.selectOne(UserDO.of().setId(userId));
        if (userDO == null) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        UserDTO userDTO = userMapper.selectUser(userDO.getUsername());
        return new ObjectRestResponse<>(ResultCode.SUCCESS, userDTO);
    }

    /**
     * 获取某一个用户的基本信息
     *
     * @return
     */
    @Override
    public ObjectRestResponse<UserDTO> getOneUserByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO userDTO = userMapper.selectUser(username);
        return new ObjectRestResponse<>(ResultCode.SUCCESS, userDTO);
    }


    /**
     * 修改密码
     *
     * @param request
     * @param resetPassword 新旧密码
     * @return 带有新的token的UserDTO对象
     */
    @Override
    public ObjectRestResponse<UserDTO> resetPassword(HttpServletRequest request, ResetPassword resetPassword) {

        String authorization = request.getHeader(TOKEN_HEADER);
        String username = JwtUtil.getUsernameFromToken(authorization, jwtProperty);
        if (StringUtils.isEmpty(username)) {
            return new ObjectRestResponse<>(ResultCode.TOKEN_INVALID);
        }
        UserDO user = userMapper.selectOne(UserDO.of().setUsername(username));
        if (null == user) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        if (!encoder.matches(resetPassword.getOldPassword(), user.getPassword())) {
            return new ObjectRestResponse<>(ResultCode.PASSWORD_IS_ERROR);
        }
        if (encoder.matches(resetPassword.getNewPassword(), user.getPassword())) {
            return new ObjectRestResponse<>(ResultCode.PASSWORD_SAME);
        }
        user.setPassword(encoder.encode(resetPassword.getNewPassword()));
        user.setLastPasswordResetDate(new Date());

        int updateRes = userMapper.updateById(user);
        if (updateRes <= 0) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        UserDTO userDTO = UserDTO.of();
        BeanUtils.copyProperties(user, userDTO);
        final String token = createToken(username, jwtProperty);
        return new ObjectRestResponse<>(ResultCode.SUCCESS, userDTO.setToken(token));
    }


    @Override
    public ObjectRestResponse<Void> changePassword(ResetPassword changePassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDO user = userMapper.selectOne(UserDO.of().setUsername(username));
        if (null == user) {
            return new ObjectRestResponse<>(ResultCode.USER_LOGIN_ERROR);
        }
        //判断原密码是否正确
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        if (!encoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            return new ObjectRestResponse<>(ResultCode.PASSWORD_IS_ERROR);
        }
        //判断新密码是否与原密码重复
        if (encoder.matches(changePassword.getNewPassword(), user.getPassword())) {
            return new ObjectRestResponse<>(ResultCode.PASSWORD_SAME);
        }

        //设置新密码,并增加密码修改时间
        user.setPassword(encoder.encode(changePassword.getNewPassword()));
        user.setLastPasswordResetDate(new Date());
        int updateRes = userMapper.updateById(user);
        if (updateRes <= 0) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS);
    }

    /**
     * 新增用户
     *
     * @param userAddDTO
     * @return
     */
    @Override
    public ObjectRestResponse<UserDTO> addUser(UserAddDTO userAddDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer adminId = userMapper.selectOne(UserDO.of().setUsername(username)).getId();
        if (1 != userRoleMapper.selectOne(UserRoleDO.of().setUserId(adminId)).getRoleId()) {//只有管理员有增加用户的权限
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }

        //新创建用户密码为123456
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        if (StringUtils.isEmpty(userAddDTO.getPassword())) {
            userAddDTO.setPassword(encoder.encode("123456"));
        }
        //新建用户是否已存在
        UserDO isExist = userMapper.selectOne(UserDO.of().setUsername(userAddDTO.getUsername()));
        if (isExist != null) {
            return new ObjectRestResponse<>(ResultCode.USER_ISEXITE);
        }
        //新建用户所以在的网关
        if (userAddDTO.getGwId()==null){
            return new ObjectRestResponse<>(ResultCode.PARAMETER_IS_WRONG);
        }
        GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(userAddDTO.getGwId()));
        if (gatewayDO==null){
            return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_EXIST);
        }
        UserDO userDO = UserDO.of().setUsername(userAddDTO.getUsername())
                .setPassword(encoder.encode(userAddDTO.getPassword())).setRealName(userAddDTO.getRealName())
                .setRegisterTime(new Date()).setDepartment(userAddDTO.getDepartment())
                .setLastPasswordResetDate(new Date())
                .setEmail(userAddDTO.getEmail()).setNumber(userAddDTO.getNumber())
                .setTel(userAddDTO.getTel()).setRemark(userAddDTO.getRemark())
                .setLoginTime(new Date());
        //插入用户列表
        Integer flag = userMapper.insert(userDO);
        if (flag < 0) {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }
        //插入用户角色列表，为普通用户
        UserRoleDO userRoleDO = UserRoleDO.of().setUserId(userDO.getId()).setRoleId(2);
        Integer flag1 = userRoleMapper.insert(userRoleDO);
        if (flag1 < 0) {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }
        //插入用户网关列表
        GwUserDO gwUserDO = GwUserDO.of().setGwId(userAddDTO.getGwId())
                .setUseFlag(userAddDTO.getUseFlag())
                .setUserId(userDO.getId());

        Integer flag2 = gwUserMapper.insert(gwUserDO);
        if (flag2 <= 0) {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }

        UserDTO userDTO = UserDTO.of();

        List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("user_id", userDO.getId()));
        userDTO.setGwIds(gwUserDOList);
        BeanUtils.copyProperties(userDO, userDTO);

        return new ObjectRestResponse<>(ResultCode.SUCCESS, userDTO);

    }

    /**
     * 修改用户基本信息
     *
     * @param userDO 用户信息对象
     * @return
     */
    @Override
    public ObjectRestResponse<UserDTO> resetInfo(UserDO userDO) {

        UserDO isExist = userMapper.selectOne(UserDO.of().setUsername(userDO.getUsername()));
        if (null == isExist) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        if (null == userDO.getId()) {
            return new ObjectRestResponse<>(ResultCode.PARAMETER_IS_WRONG);
        }
        if (userMapper.selectOne(UserDO.of().setId(userDO.getId())) == null) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        if (null != userDO.getPassword()) {//管理员不能更新用户密码
            userDO.setPassword(null);
        }
//        if(StringUtils.isEmpty(userDO.getRealName())){
//            return new ObjectRestResponse<>(ResultCode.Parameter_IS_NULL);
//        }

        userMapper.updateById(userDO);
        UserDO returnUser = userMapper.selectOne(UserDO.of().setId(userDO.getId()));
        UserDTO userDTO = UserDTO.of();
        //不包含管理员
        List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("user_id", userDO.getId()));
        userDTO.setGwIds(gwUserDOList);
        BeanUtils.copyProperties(returnUser, userDTO);
        return new ObjectRestResponse<UserDTO>(ResultCode.SUCCESS, userDTO);
    }

    /**
     * 删除某一个用户
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public ObjectRestResponse<Boolean> deleteUserInfo(Integer userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer adminId = userMapper.selectOne(UserDO.of().setUsername(username)).getId();
        if (1 != userRoleMapper.selectOne(UserRoleDO.of().setUserId(adminId)).getRoleId()) {//只有管理员有删除用户权限
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        UserDO userDO = userMapper.selectOne(UserDO.of().setId(userId));
        if (null == userDO) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        Integer flag = userMapper.deleteById(userId);
        if (flag > 0) {
            return new ObjectRestResponse<Boolean>(ResultCode.SUCCESS, true);
        } else {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }
    }

    /**
     * 批量删除用户
     *
     * @param userIds
     * @return
     */
    @Override
    public ObjectRestResponse<Boolean> deleteUserInfoBatch(String userIds) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer adminId = userMapper.selectOne(UserDO.of().setUsername(username)).getId();
        if (1 != userRoleMapper.selectOne(UserRoleDO.of().setUserId(adminId)).getRoleId()) {//只有管理员有删除用户权限
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        List<Integer> ids = new ArrayList<>();
        String[] arr = userIds.split("-");
        for (int i = 0; i < arr.length; i++) {
            UserDO userDO = userMapper.selectOne(UserDO.of().setId(Integer.parseInt(arr[i])));
            if (null == userDO) {
                return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
            }
            ids.add(Integer.parseInt(arr[i]));
        }

        Integer flag = userMapper.deleteBatchIds(ids);
        if (flag > 0) {
            return new ObjectRestResponse<Boolean>(ResultCode.SUCCESS, true);
        } else {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }

    }

    /**
     * 控制启用和禁用
     *
     * @param userId
     * @param gwId
     * @return
     */
    @Override
    public ObjectRestResponse<Boolean> permitInfo(Integer userId, Integer gwId) {
        GwUserDO gwUserDO = gwUserMapper.selectOne(GwUserDO.of().setGwId(gwId).setUserId(userId));

        Boolean status = false;
        if (1 == gwUserDO.getUseFlag()) {
            gwUserDO.setUseFlag(0);
            status = false;
        } else {
            gwUserDO.setUseFlag(1);
            status = true;
        }
        Integer flag = gwUserMapper.updateById(gwUserDO);
        if (flag <= 0) {
            return new ObjectRestResponse<Boolean>(ResultCode.DATA_IS_WRONG);
        } else {
            return new ObjectRestResponse<Boolean>(ResultCode.SUCCESS, status);
        }

    }

    /**
     * 管理员恢复用户初始密码
     *
     * @param username 用户名
     * @return
     */
    @Override
    public ObjectRestResponse<Void> initPassword(String username) {
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        int res = userMapper.update(UserDO.of().setPassword(encoder.encode("123456"))
                        .setLastPasswordResetDate(new Date()).setResetPassword("0"),
                new EntityWrapper<UserDO>().eq("username", username));
        if (res == 1) {
            return new ObjectRestResponse<>(ResultCode.SUCCESS);
        } else {
            return new ObjectRestResponse<>(ResultCode.USER_ISERROR);
        }
    }


}
