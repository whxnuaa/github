package com.jit.skiad.bean;

import lombok.Data;

import java.util.List;

@Data
public class RecieveBean {
    private String terminal;//终端类型
    private Integer id;//网关ID
    private String msgType;//消息类型
    private String from; //APP的Socket
    private String to;//APP的Socket
    private String order;//指令类型
    private String stop;//启动，继电器addr+路
    private String run;//停止，继电器addr+路
    private List<ReportDataBean> content;//数据上报内容
    private Integer opener_addr;//开度仪地址
    private Integer liquidLevel_addr;//开度仪地址
    private Integer operation;//1为开启自动控制,0 关闭
    private Float waterLevel;//预设水位，单位：米
    private Float gateHeight;//预设闸门高度
    private String up;//控制进水闸门上升，手动控制
    private String down;//控制进水闸门下降，手动控制
    private String in_up;//控制进水闸门上升，自动控制
    private String in_down;//控制进水闸门下降，自动控制
    private String out_up;//控制出水闸门上升指令，自动控制
    private String out_down;//控制出水闸门下降指令，自动控制
    private String result;//网关设置结果 成功：success，失败：false

}
