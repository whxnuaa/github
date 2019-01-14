package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.domain.CommondataDO;
import com.jit.skiad.dto.WaterDTO;
import com.jit.skiad.serviceinterface.WaterService;
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

import java.util.List;
import java.util.Map;

@Api(value = " 水位管理",description = "水位管理")
@RestController
@RequestMapping("water")
@Slf4j
public class WaterController {

    @Autowired
    private WaterService waterService;

    /**
     * 获取历史水位
     * @param gwId
     * @param startTime
     * @param endTime
     * @return
     */
    @ApiOperation(value = "获取历史水位",notes = "获取历史水位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int"),
            @ApiImplicitParam(name="startTime",value = "起始时间",required = true,dataType = "string"),
            @ApiImplicitParam(name="endTime",value = "结束时间",required = true,dataType = "string")
    })
    @GetMapping("user/data")
    ObjectRestResponse<WaterDTO> getDataByTime(@RequestParam("gwId") Integer gwId, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime){
        return waterService.getDataByTime(gwId, startTime, endTime);
    }

    @ApiOperation(value = "获取当前水位和预警状态",notes = "获取当前水位和预警状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("user/current")
    ObjectRestResponse<WaterDTO> getCurrentData(@RequestParam("gwId")Integer gwId){
        return waterService.getCurrentData(gwId);
    }


}
