package com.jit.skiad.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 水泵
 */
@Accessors(chain = true)
@Data
public class PumpDTO {
    private Integer id;
    private String number;
    private char status;//设备开关
    private Float runtimes;
    private Integer alarm;//预警状态
    //电压
    private Float vDataValue1;
    private Float vDataValue2;
    private Float vDataValue3;
    //电流
    private Float eDataValue1;
    private Float eDataValue2;
    private Float eDataValue3;
}
