
package com.jit.skiad.controller;

import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.PumpDTO;
import com.jit.skiad.serviceinterface.PumpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = " 水泵管理",description = "水泵管理")
@RestController
@RequestMapping("pump")
@Slf4j
public class PumpController {

    @Autowired
    private PumpService pumpService;
    
    /**
     * 获取水泵信息
     * @param gwId 分厂,一个分厂可能对应多个水泵
     * @return
     */
    @ApiOperation(value = "获取水泵信息",notes = "获取水泵信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "gwId", value = "网关id", required = true, dataType = "int")
    })
    @GetMapping("user/data")
    ObjectRestResponse<List<PumpDTO>> getPumpData(@RequestParam("gwId") Integer gwId){
//        redisTest.testObj();
        return pumpService.getPumpData(gwId);
    }

    /**
     * 控制设备开关
     * @return
     */
    @ApiOperation(value = "控制设备开关",notes = "控制设备开关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "该参数值（value='Bearer {token}'）在request header中", paramType ="header", required = true, dataType = "String"),
            @ApiImplicitParam(name = "controlDTO", value = "控制对象", required = true, dataType = "ControlDTO")
    })
    @PutMapping("user/status")
    ObjectRestResponse<PumpDTO> updatePumpStatus(@RequestBody ControlDTO controlDTO){
        return pumpService.updatePumpData(controlDTO);
    }
}
