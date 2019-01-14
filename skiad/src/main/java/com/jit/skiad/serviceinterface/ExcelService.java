package com.jit.skiad.serviceinterface;

import javax.servlet.http.HttpServletResponse;

public interface ExcelService {

    void exportAlarmExcel(HttpServletResponse response,Integer gwId) throws Exception;

    void exportWaterHistoryExcel(HttpServletResponse response,Integer gwId, String startTime, String endTime) throws Exception;
}
