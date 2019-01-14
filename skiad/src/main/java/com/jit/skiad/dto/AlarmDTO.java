package com.jit.skiad.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmDTO {
    private Integer gwId;//网关id
    private String reason;//原因
    private String operation;//操作员
    private Date time;//时间
    private String remark;//备注

}
