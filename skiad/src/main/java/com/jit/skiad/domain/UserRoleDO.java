package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
@TableName("user_role")
public class UserRoleDO {
    private Integer id;
    private Integer userId;
    private Integer roleId;
}
