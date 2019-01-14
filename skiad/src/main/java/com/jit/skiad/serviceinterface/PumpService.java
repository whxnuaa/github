package com.jit.skiad.serviceinterface;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.PumpDTO;

import java.util.List;

public interface PumpService {
    public ObjectRestResponse<List<PumpDTO>> getPumpData(Integer gwId);

    public ObjectRestResponse<PumpDTO> updatePumpData(ControlDTO pumpControlDTO);
}
