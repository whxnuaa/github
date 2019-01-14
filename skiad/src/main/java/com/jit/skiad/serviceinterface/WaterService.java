package com.jit.skiad.serviceinterface;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.CommondataDO;
import com.jit.skiad.dto.WaterDTO;

import java.util.List;
import java.util.Map;

public interface WaterService {

    ObjectRestResponse<WaterDTO> getDataByTime(Integer type, String startTime, String endTime);
    WaterDTO getHistory(Integer gwId, String startTime, String endTime);
    ObjectRestResponse<WaterDTO> getCurrentData(Integer gwId);

}
