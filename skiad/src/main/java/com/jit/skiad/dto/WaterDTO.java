package com.jit.skiad.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WaterDTO {
    private Integer gwId;
    private Map<String, List> res;
    private Float waterLevel;
    private Integer alarm;

}
