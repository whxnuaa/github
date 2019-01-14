package com.jit.skiad.bean;

import java.util.List;

public class DeviceOnlineBean {
    private Integer id; // 网关ID
    private List<ReportDataBean> sensors; //在线传感器列
    private List<RelayBean> relays; //在线继电器各路状态
    private String address; //网关IP

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ReportDataBean> getSensors() {
        return sensors;
    }

    public void setSensors(List<ReportDataBean> sensors) {
        this.sensors = sensors;
    }

    public List<RelayBean> getRelays() {
        return relays;
    }

    public void setRelays(List<RelayBean> relays) {
        this.relays = relays;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "DeviceOnlineBean{" +
                "id=" + id +
                ", sensors=" + sensors +
                ", relays=" + relays +
                ", address='" + address + '\'' +
                '}';
    }

}
