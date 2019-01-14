package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.bean.DeviceOnlineBean;
import com.jit.skiad.bean.RelayBean;
import com.jit.skiad.bean.ReportDataBean;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.commons.client.ConnectMina;

import com.jit.skiad.commons.util.SendMessageUtil;
import com.jit.skiad.domain.*;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.GateDTO;
import com.jit.skiad.dto.GatePumpDTO;
import com.jit.skiad.mapper.*;
import com.jit.skiad.serviceinterface.GateService;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GateServiceImpl implements GateService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GateMapper gateMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private GwUserMapper gwUserMapper;

    @Autowired
    private PumpMapper pumpMapper;


    /**
     * 获取闸门数据
     *
     * @return
     */
    @Override
    public ObjectRestResponse<List<GateDTO>> getGateData(Integer gwId) {

        List<GateDO> gateDOList = gateMapper.selectList(new EntityWrapper<GateDO>().eq("gw_id", gwId));
        List<GateDTO> gateDTOList = new ArrayList<>();
        String key = "GW" + gwId;
        ValueOperations<String, DeviceOnlineBean> operations = redisTemplate.opsForValue();
        //redis缓存中有，从缓存中获取
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            DeviceOnlineBean deviceOnline = operations.get(key);
            System.out.println("gate result=====" + deviceOnline);
            if (null == deviceOnline) {
                return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);//通过key获取的数据为空
            }
            for (int k = 0; k < gateDOList.size(); k++) {//多个闸门的数据值
                GateDTO gateDTO = new GateDTO();
                gateDTO.setNumber(gateDOList.get(k).getNumber());
                gateDTO.setGwId(deviceOnline.getId());

                //继电器数据
                List<RelayBean> relayBeanList = deviceOnline.getRelays();
                char upStatus = 0;
                char downStatus = 0;
                if (relayBeanList.size() == 0) {
                    return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
                }
                for (int i = 0; i < relayBeanList.size(); i++) {

                    if (relayBeanList.get(i).getAddr485().equals(gateDOList.get(k).getRelayAddr())
                            && (relayBeanList.get(i).getPosition().equals(gateDOList.get(k).getRelayPositionUp() + 3))) {//1路对应的设备
                        upStatus = relayBeanList.get(i).getStatus();
                    }
                    if (relayBeanList.get(i).getAddr485().equals(gateDOList.get(k).getRelayAddr())
                            && (relayBeanList.get(i).getPosition().equals(gateDOList.get(k).getRelayPositionDown() + 3))) {//2路对应的设备
                        downStatus = relayBeanList.get(i).getStatus();
                    }
                }

                if (upStatus == '1' || downStatus == '1') {
                    gateDTO.setStatus('1');//设备的开关
                } else {
                    gateDTO.setStatus('0');//设备的开关
                }

                DecimalFormat df = new DecimalFormat("#.##");

                gateDTO.setNumber(gateDOList.get(k).getNumber());//设备编号
                List<ReportDataBean> reportDataBeanList = deviceOnline.getSensors();
                for (int i = 0; i < reportDataBeanList.size(); i++) {
                    if (reportDataBeanList.get(i).getAddr().equals(gateDOList.get(k).getGateopenerAddr())
                            && reportDataBeanList.get(i).getType().equals("gateOpener")) {//高度
                        Float opener = reportDataBeanList.get(i).getValue().get(0) / 1000;
                        opener = Float.parseFloat(df.format(opener));
                        gateDTO.setGateOpener(opener);
                    }
                    if (reportDataBeanList.get(i).getAddr().equals(gateDOList.get(k).getLiquidAddr())
                            && (reportDataBeanList.get(i).getType().equals("liquidLevel"))) {
                        Float level = reportDataBeanList.get(i).getValue().get(0) / 1000;
                        level = Float.parseFloat(df.format(level));
                        gateDTO.setLiquidLevel(level);//水位值
                    }
                }
                gateDTO.setGateHeight(gateDOList.get(k).getMaxHeight());
                gateDTOList.add(gateDTO);
            }
        } else {
            return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);//缓冲中没有数据，网关不在线
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS, gateDTOList);

    }

    /**
     * 手动控制闸门
     *
     * @param controlDTO
     * @return
     */
    @Override
    public ObjectRestResponse<GateDTO> updateGateStatus(ControlDTO controlDTO) {
        //被启用的用户具有控制权限
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDO userDO = userMapper.selectOne(UserDO.of().setUsername(username));
        GwUserDO gwUserDO = gwUserMapper.selectOne(GwUserDO.of().setGwId(controlDTO.getId()).setUserId(userDO.getId()));

        if (null == gwUserDO) {
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        if ((null != gwUserDO) && (0 == gwUserDO.getUseFlag())) {
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        //查找数据库中对应的控制
        GateDO isExist = gateMapper.selectOne(GateDO.of().setGwId(controlDTO.getId()).setNumber(controlDTO.getNumber()));
        if (isExist == null) {
            return new ObjectRestResponse<>(ResultCode.DEVICE_NOT_EXIST);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("terminal", "web");
        map.put("msgType", "control_gate");
        map.put("id", controlDTO.getId());
        map.put("opener_addr", isExist.getGateopenerAddr());
        Float height = isExist.getMaxHeight() * controlDTO.getPercent() / 100;
        map.put("height", height);
        String up = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionUp();
        map.put("up", up);
        String down = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionDown();
        map.put("down", down);
        String stop = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionStop();
        map.put("stop", stop);
        ObjectRestResponse<DeviceOnlineBean> sendControl = SendMessageUtil.sendQueryOrControl(map);
        if (sendControl.getCode().equals("00000")) {
            DeviceOnlineBean deviceOnline = sendControl.getData();
            GateDTO gateDTO = new GateDTO();
            gateDTO.setNumber(isExist.getNumber());
            gateDTO.setGwId(controlDTO.getId());
            DecimalFormat df = new DecimalFormat("#.##");
            List<ReportDataBean> reportDataBeanList = deviceOnline.getSensors();
            for (int i = 0; i < reportDataBeanList.size(); i++) {
                if (reportDataBeanList.get(i).getAddr().equals(isExist.getGateopenerAddr())
                        && reportDataBeanList.get(i).getType().equals("gateOpener")) {//高度

                    Float opener = reportDataBeanList.get(i).getValue().get(0) / 1000;
                    opener = Float.parseFloat(df.format(opener));
                    gateDTO.setGateOpener(opener);
                }
                if (reportDataBeanList.get(i).getAddr().equals(isExist.getLiquidAddr())
                        && (reportDataBeanList.get(i).getType().equals("liquidLevel"))) {
                    Float level = reportDataBeanList.get(i).getValue().get(0) / 1000;
                    level = Float.parseFloat(df.format(level));
                    gateDTO.setLiquidLevel(level);//水位值
                }
            }

            //继电器数据
            List<RelayBean> relayBeanList = deviceOnline.getRelays();
            char upStatus = 0;
            char downStatus = 0;
            for (int i = 0; i < relayBeanList.size(); i++) {
                if (relayBeanList.get(i).getAddr485().equals(isExist.getRelayAddr())) {
                    if (relayBeanList.get(i).getPosition().equals(isExist.getRelayPositionUp() + 3)) {//1路对应的设备
                        upStatus = relayBeanList.get(i).getStatus();
                    }
                    if (relayBeanList.get(i).getPosition().equals(isExist.getRelayPositionDown() + 3)) {//3路对应的设备
                        downStatus = relayBeanList.get(i).getStatus();
                    }
                }
            }
            if (upStatus == '1' || downStatus == '1') {
                gateDTO.setStatus('1');//设备的开关
            } else {
                gateDTO.setStatus('0');//设备的开关
            }
            gateDTO.setGateHeight(isExist.getMaxHeight());//闸门总高
            return new ObjectRestResponse<GateDTO>(ResultCode.SUCCESS, gateDTO);
        } else {
            return new ObjectRestResponse<>(sendControl.getCode(), sendControl.getMessage(), null);
        }
    }

    @Override
    public ObjectRestResponse<List<GatePumpDTO>> getGatePumpInfo() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDO userDO = userMapper.selectOne(UserDO.of().setUsername(username));
        if (userDO != null) {
            List<GatePumpDTO> gatePumpDTOList = new ArrayList<>();

            //获取网关list表
            List<GatewayDO> gatewayDOList = gatewayMapper.selectList(null);
//            List<GwUserDO> gwUserDOList = gwUserMapper.selectList(new EntityWrapper<GwUserDO>().eq("user_id",userDO.getId()));
            if (gatewayDOList.size() != 0) {
                for (int i = 0; i < gatewayDOList.size(); i++) {
                    GatePumpDTO gatePumpDTO = new GatePumpDTO();
                    //增加闸门list信息
                    List<GateDO> gateDOList = gateMapper.selectList(new EntityWrapper<GateDO>().eq("gw_id", gatewayDOList.get(i).getId()));
                    gatePumpDTO.setGateDOList(gateDOList);
                    //增加水泵list信息
                    List<PumpDO> pumpDOList = pumpMapper.selectList(new EntityWrapper<PumpDO>().eq("gw_id", gatewayDOList.get(i).getId()));
                    gatePumpDTO.setPumpDOList(pumpDOList);
                    gatePumpDTO.setUsername(username);
                    gatePumpDTO.setGwId(gatewayDOList.get(i).getId());
                    gatePumpDTOList.add(gatePumpDTO);

                }
            }
            return new ObjectRestResponse<>(ResultCode.SUCCESS, gatePumpDTOList);
        } else {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
    }
}
