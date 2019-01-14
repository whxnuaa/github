package com.jit.skiad.bean;


import java.util.List;

public class ReportDataBean {
    private Integer addr;//传感器485地址
    private String type;//传感器类型
    private List<Float> value;//检测值（使用list的原因：电压电流上报三个值，其他一个值）

    public Integer getAddr() {
        return addr;
    }

    public void setAddr(Integer addr) {
        this.addr = addr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Float> getValue() {
        return value;
    }

    public void setValue(List<Float> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ReportDataBean{" +
                "addr=" + addr +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
