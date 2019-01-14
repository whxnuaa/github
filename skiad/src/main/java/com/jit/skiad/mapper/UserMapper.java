package com.jit.skiad.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.dto.JwtUser;
import com.jit.skiad.dto.UserDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<UserDO> {

    @Select("<script>" +
            "select u.id AS id,u.username,u.real_name AS realName,u.image,u.register_time AS registerTime ," +
            "u.tel,u.email,u.number,u.department,u.remark,u.login_time as loginTime, ur.role_id as roleId , r.name as roleName " +
            "FROM user u" +
            " left join user_role ur on ur.user_id = u.id " +
            "left join role r on r.id = ur.role_id " +
            "<if test='username!=null'> where  username=#{username}</if>" +
            "</script>")
    List<UserDTO> selectLists(@Param("username") String username);

    @Select("select u.id AS id,u.username,u.real_name AS realName,u.image,u.register_time AS registerTime,u.tel,u.email,u.number,u.department,u.remark,  ur.role_id as roleId , r.name as roleName  from user u left join user_role ur on ur.user_id=u.id left join role r on r.id=ur.role_id where username=#{username}")
    UserDTO selectUser(@Param("username")String username);


}
