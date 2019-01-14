package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
//@AllArgsConstructor
//@NoArgsConstructor
@TableName("user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String image;
    private Date registerTime;
    private Date lastPasswordResetDate;
    private Date loginTime;
    private String tel;
    private String email;
    private String number;
    private String department;
    private String remark;
    private String resetPassword;//是否需要恢复初始密码：0：不需要，1：需要
//    private Integer useFlag;//是否启用

}
