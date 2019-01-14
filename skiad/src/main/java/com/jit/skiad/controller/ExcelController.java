package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.AlarmDO;
import com.jit.skiad.dto.GateDTO;
import com.jit.skiad.serviceinterface.ExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = " Excel管理",description = "Excel管理")
@RestController
@RequestMapping("excel")
@Slf4j
public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @ApiOperation(value = "获取预警Excel表",notes = "获取预警Excel表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("alarm")
    void getGateData(HttpServletResponse response, @RequestParam("gwId")Integer gwId) throws Exception {
       excelService.exportAlarmExcel(response, gwId);
    }
    @ApiOperation(value = "获取历史水位Excel表",notes = "获取历史水位Excel表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int"),
            @ApiImplicitParam(name="startTime",value = "起始时间",required = true,dataType = "string"),
            @ApiImplicitParam(name="endTime",value = "结束时间",required = true,dataType = "string")
    })
    @GetMapping("water")
    void getWaterData(HttpServletResponse response, @RequestParam("gwId")Integer gwId, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) throws Exception {
        excelService.exportWaterHistoryExcel(response, gwId,startTime,endTime);
    }
}
