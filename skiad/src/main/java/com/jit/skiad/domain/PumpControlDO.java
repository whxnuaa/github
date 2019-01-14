package com.jit.skiad.domain;

import lombok.Data;

import java.util.Date;

@Data
public class PumpControlDO {
    private Integer id;
    private Integer gwId;
    private Integer type;
    private String username;
    private Integer pumpId;
    private Date time;
}
