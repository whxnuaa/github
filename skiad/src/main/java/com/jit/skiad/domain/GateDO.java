package com.jit.skiad.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@TableName("gate")
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class GateDO {
    private Integer id;
    private Integer gwId;//网关ID
    private String number;//闸门编号
    private Integer relayAddr;//继电器485地址
    private Integer relayPositionUp;//控制砸门上升的继电器位
    private Integer relayPositionDown;//控制闸门下降的继电器位
    private Integer relayPositionStop;//控制闸门停止
    private String affect;//该闸门的作用：in 进水，out 出水
    private Integer gateopenerAddr;//闸门开度485地址
    private Integer liquidAddr;//水位485地址
    private Float maxHeight;//最大高度

}
