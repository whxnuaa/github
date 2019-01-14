package com.jit.skiad.dto;

import lombok.Data;

@Data
public class ControlDTO {
    private Integer id;//网关id
    private Integer status;//闸门开关
    private Integer type;//控制类型，自动、手动
    private String number;//闸门编号
    private Integer percent;//开度百分百
}
