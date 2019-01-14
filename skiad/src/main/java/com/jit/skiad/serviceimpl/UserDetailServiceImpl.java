package com.jit.skiad.serviceimpl;

import com.jit.skiad.domain.RoleDO;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.dto.JwtUser;
import com.jit.skiad.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RoleMapper roleMapper;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDO user = userMapper.selectOne(UserDO.of().setUsername(username));
        if (user != null) {
            List<RoleDO> roles = roleMapper.getRoleByUserId(user.getId());
            return new JwtUser(user.getId(),user.getUsername(),user.getPassword(),roles,user.getLastPasswordResetDate());
//            return new JwtUser(user.getId(),user.getUsername(),user.getRealName(),user.getPassword(),roles,user.getLastPasswordResetDate());
        } else {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
    }

}
