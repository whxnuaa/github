package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@TableName("pump")
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class PumpDO {
    private Integer id;//水泵ID
    private Integer gwId;//网关ID
    private String number;//水泵编号
    private Integer voltageAddr;//电压传感器485地址
    private Integer electricityAddr;//电流传感器485地址
    private Integer runtimesAddr;//运行时间addr
    private Integer relayAddr;//继电器485地址
    private Integer relayPositionRun;//继电器启动位, 第1路0位
    private Integer relayPositionStop;//继电器停止位, 第2路1位
}
