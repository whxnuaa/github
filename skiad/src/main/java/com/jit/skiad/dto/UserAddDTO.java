package com.jit.skiad.dto;

import lombok.Data;

@Data
public class UserAddDTO {
    private String username;
    private String password;
    private String realName;
    private String tel;
    private String email;
    private String number;
    private String department;
    private String remark;
    private Integer useFlag;
    private Integer gwId;

}
