package com.jit.skiad.serviceinterface;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.GateDTO;
import com.jit.skiad.dto.GatePumpDTO;

import java.util.List;

public interface GateService {
    public ObjectRestResponse<List<GateDTO>> getGateData(Integer gwId);
    public ObjectRestResponse<GateDTO> updateGateStatus(ControlDTO controlDTO);
    public ObjectRestResponse<List<GatePumpDTO>> getGatePumpInfo();
}
