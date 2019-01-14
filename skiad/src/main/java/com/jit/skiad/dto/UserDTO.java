package com.jit.skiad.dto;

import com.jit.skiad.domain.GwUserDO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
//@NoArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class UserDTO {
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
    private Integer roleId;
    private String roleName;
    private String token;
    private List<GwUserDO> gwIds;
    private Integer useFlag;
}
