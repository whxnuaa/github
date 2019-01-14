package com.jit.skiad.dto;

import com.jit.skiad.domain.GateDO;
import com.jit.skiad.domain.PumpDO;
import lombok.Data;

import java.util.List;

@Data
public class GatePumpDTO {
    private String username;
    private Integer gwId;
    private List<GateDO> gateDOList;
    private List<PumpDO> pumpDOList;
}
