package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.domain.GatewayDO;
import com.jit.skiad.domain.GwUserDO;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.domain.UserRoleDO;
import com.jit.skiad.dto.JwtUser;
import com.jit.skiad.dto.UserDTO;
import com.jit.skiad.jwtsecurity.bean.JwtProperty;
import com.jit.skiad.jwtsecurity.util.CustomPasswordEncoder;
import com.jit.skiad.mapper.*;
import com.jit.skiad.serviceinterface.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jit.skiad.jwtsecurity.util.JwtUtil.createToken;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    UserDetailServiceImpl userDetailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    private JwtProperty property;
    @Autowired
    private GwUserMapper gwUserMapper;
    @Autowired
    private GatewayMapper gatewayMapper;


    /**
     * 用户注册
     * @param user 用户信息
     * @return
     */
    @Override
    public ObjectRestResponse<Void> register(UserDTO user) {
        if(null == user.getRoleId() || 0 == user.getRoleId()){
            return new ObjectRestResponse<Void>(ResultCode.DATA_IS_WRONG);
        }
        List<UserDO> users = userMapper.selectList(new EntityWrapper<UserDO>().eq("username",user.getUsername()));
        if(null != users && users.size() > 0){
            return new ObjectRestResponse<Void>(ResultCode.USER_ISEXITE);
        }
        user.setRegisterTime(new Date());
        user.setLastPasswordResetDate(new Date());
        String passWord = user.getPassword();
        PasswordEncoder encoder = new CustomPasswordEncoder();
        user.setPassword(encoder.encode(passWord));
        UserDO userDO = UserDO.of();
        BeanUtils.copyProperties(user,userDO);
        userMapper.insert(userDO);
        log.info("user 2:=== {}",userDO);
        if(null != userDO.getId()) {
            userRoleMapper.insert(UserRoleDO.of().setRoleId(user.getRoleId()).setUserId(userDO.getId()));
        }else {
            return new ObjectRestResponse<>(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS);

    }

    @Override
    public ObjectRestResponse<UserDTO> login(String username, String password) {
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();

        UserDO userDO = userMapper.selectOne(UserDO.of().setUsername(username));
        if (null == userDO || !encoder.matches(password,userDO.getPassword())){
            return new ObjectRestResponse<>(ResultCode.USER_LOGIN_ERROR);
        }
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username,password);
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtUser userDetails = (JwtUser)authentication.getPrincipal();

        final String token = createToken(username,property);
        UserDTO userDTO = UserDTO.of();

        BeanUtils.copyProperties(userDetails,userDTO);
        userDO.setLoginTime(new Date());
        userDTO.setToken(token)
                .setRealName(userDO.getRealName())
                .setRoleName(userDetails.getRoles().get(0).getName())
                .setRoleId(userDetails.getRoles().get(0).getId())
                .setRegisterTime(userDO.getRegisterTime())
                .setLastPasswordResetDate(userDO.getLastPasswordResetDate())
                .setLoginTime(userDO.getLoginTime())
                .setPassword(null);


        List<GwUserDO> gwIds = new ArrayList<>();
//        if (userDetails.getRoles().get(0).getId() == 1){//管理员角色，有所有网关的权限
//            List<GatewayDO> gatewayDOList = gatewayMapper.selectList(null);
//            GwUserDO gwUserDO = GwUserDO.of();
//            for (int i=0;i<gatewayDOList.size();i++){
//                gwUserDO.setUseFlag(1);
//                gwUserDO.setGwId(gatewayDOList.get(i).getId());
//                gwIds.add(gwUserDO);
//            }
//            userDTO.setGwIds(gwIds);
//        }else {//普通用户角色，根据网关用户表确定权限
            List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("user_id",userDO.getId()));
            userDTO.setGwIds(gwUserDOList);
//        }

        userMapper.updateById(userDO);
        return new ObjectRestResponse<UserDTO>(ResultCode.SUCCESS,userDTO);
    }

    @Override
    public ObjectRestResponse<String> refresh(String oldToken) {
        return null;
    }

    /**
     * 用户忘记密码，请求管理员重置密码
     * @param username 用户名
     * @return
     */
    @Override
    public ObjectRestResponse<Void> forgetPassWord(String username,String number) {
        UserDO isExist = userMapper.selectOne(UserDO.of().setUsername(username));
        if (isExist!=null){
            UserDO is = userMapper.selectOne(UserDO.of().setUsername(username).setNumber(number));
            if (is ==null){
                return new ObjectRestResponse<>(ResultCode.NUMBER_ISERROR);
            }else {
                UserRoleDO userRoleDO = userRoleMapper.selectOne(UserRoleDO.of().setUserId(is.getId()));
                if (userRoleDO.getRoleId() == 1){
                    return new ObjectRestResponse<>(ResultCode.ADMIN_RESET_DENYED);
                }
            }
        }else {
            return new ObjectRestResponse<>(ResultCode.USER_ISERROR);
        }

        int res = userMapper.update(UserDO.of().setResetPassword("1"),new EntityWrapper<UserDO>().eq("username",username).eq("number",number));
        if(res == 1){
            return new ObjectRestResponse<>(ResultCode.SUCCESS);
        }else{
            return new ObjectRestResponse<>(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
        }
    }

}
