package com.jit.skiad.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jit.skiad.domain.RoleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper extends BaseMapper<RoleDO> {

    @Select("select r.* from role r where r.id = (  select role_id from user_role where user_id = #{user_id})")
    List<RoleDO> getRoleByUserId(@Param("user_id")Integer user_id);
}
