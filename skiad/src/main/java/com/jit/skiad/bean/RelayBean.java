package com.jit.skiad.bean;


public class RelayBean {
    private Integer addr485;//485地址
    private Integer position; //第几路
    private char status; //状态，0：关闭，1：打开

    public Integer getAddr485() {
        return addr485;
    }

    public void setAddr485(Integer addr485) {
        this.addr485 = addr485;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public RelayBean() {
    }

    public RelayBean(Integer addr485, Integer position, char status) {
        this.addr485 = addr485;
        this.position = position;
        this.status = status;
    }

    @Override
    public String toString() {
        return "RelayBean{" +
                "addr485=" + addr485 +
                ", position=" + position +
                ", status=" + status +
                '}';
    }
}
