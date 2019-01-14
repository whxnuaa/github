package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.AlarmDO;
import com.jit.skiad.domain.GatewayDO;
import com.jit.skiad.serviceinterface.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = " 预警管理",description = "预警管理")
@RestController
@RequestMapping("alarm")
@Slf4j
public class AlarmController {

    @Autowired
    private AlarmService alarmService;
    @ApiOperation(value = "获取当前网关的水位",notes = "获取当前网关的水位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("user/water")
    public ObjectRestResponse<GatewayDO> getWaterLevel(@RequestParam("gwId") Integer gwId){
        return alarmService.getWaterLevel(gwId);
    }

    @ApiOperation(value = "更新当前网关的水位",notes = "更新当前网关的水位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int"),
            @ApiImplicitParam(name = "waterlevel",value = "水位",required = true,dataType = "float")
    })
    @PutMapping("user/water")
    public ObjectRestResponse<GatewayDO> updateWaterLevel(@RequestParam("gwId")Integer gwId,@RequestParam("waterlevel")Float waterlevel){
        return alarmService.updateWaterLevel(gwId, waterlevel);
    }

    @ApiOperation(value = "获得预警总数",notes = "获得预警总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String")
    })
    @GetMapping("user/number")
    public ObjectRestResponse<Integer> getAlarmNumber(){
        return alarmService.getAlarmNumber();
    }

    @ApiOperation(value = "获得某一网关的预警表",notes = "获得某一网关的预警表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("user/list")
    public ObjectRestResponse<List<AlarmDO>> getAlarmList(@RequestParam("gwId")Integer gwId){
        return alarmService.getAlarmList(gwId);
    }

}
