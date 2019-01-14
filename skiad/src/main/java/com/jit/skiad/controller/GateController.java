package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.GateDTO;
import com.jit.skiad.dto.GatePumpDTO;
import com.jit.skiad.serviceinterface.GateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = " 闸门管理",description = "闸门管理")
@RestController
@RequestMapping("gate")
@Slf4j
public class GateController {

    @Autowired
    private GateService gateService;

    /**
     * 获取闸门数据
     * @param gwId
     * @return
     */
    @ApiOperation(value = "获取闸门数据",notes = "获取闸门数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("user/data")
    ObjectRestResponse<List<GateDTO>> getGateData(@RequestParam("gwId")Integer gwId){
        return gateService.getGateData(gwId);
    }

    @ApiOperation(value = "控制闸门开关",notes = "控制闸门开关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "controlDTO", value = "控制开关对象", required = true, dataType = "ControlDTO")
    })
    @PutMapping("user/update")
    ObjectRestResponse<GateDTO> updateGateStatus(@RequestBody ControlDTO controlDTO){
        return gateService.updateGateStatus(controlDTO);
    }

    @ApiOperation(value = "获取闸门水泵信息",notes = "获取闸门水泵信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String")
    })
    @GetMapping("user/all")
    ObjectRestResponse<List<GatePumpDTO>> getGatePumpInfo(){
        return gateService.getGatePumpInfo();
    }
}
