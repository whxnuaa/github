package com.jit.skiad.serviceinterface;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.AlarmDO;
import com.jit.skiad.domain.GatewayDO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AlarmService {

     ObjectRestResponse<Void> getPumpAlarm(Integer gwId);

     ObjectRestResponse<GatewayDO> updateWaterLevel(Integer gwId,Float waterLevel);

     ObjectRestResponse<GatewayDO> getWaterLevel(Integer gwId);

     ObjectRestResponse<Integer> getAlarmNumber();

     ObjectRestResponse<List<AlarmDO>> getAlarmList(Integer gwId);
}
