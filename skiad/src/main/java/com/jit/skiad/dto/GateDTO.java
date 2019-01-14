package com.jit.skiad.dto;

import lombok.Data;


@Data
public class GateDTO {
    private Integer id;
    private Integer gwId;//网关id
    private String number;//闸门编号
    private Float gateOpener;//闸门开度
    private Float liquidLevel;//水位高度
    private char status;//运行状态
    private Float gateHeight;//闸门总高度

}
