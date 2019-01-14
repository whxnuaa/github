package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.bean.DeviceOnlineBean;
import com.jit.skiad.bean.RelayBean;
import com.jit.skiad.bean.ReportDataBean;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.commons.client.ConnectMina;
import com.jit.skiad.commons.util.ObjectUtil;
import com.jit.skiad.commons.util.SendMessageUtil;
import com.jit.skiad.domain.*;
import com.jit.skiad.dto.ControlDTO;
import com.jit.skiad.dto.PumpDTO;
import com.jit.skiad.mapper.GatewayMapper;
import com.jit.skiad.mapper.GwUserMapper;
import com.jit.skiad.mapper.PumpMapper;
import com.jit.skiad.mapper.UserMapper;
import com.jit.skiad.serviceinterface.PumpService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class PumpServiceImpl implements PumpService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PumpMapper pumpMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private GwUserMapper gwUserMapper;

    /**
     * 获取一个分厂下的多个水泵的基本信息
     *
     * @param gwId
     * @return
     */
    @Override
    public ObjectRestResponse<List<PumpDTO>> getPumpData(Integer gwId) {
        List<PumpDO> pumpDOList = pumpMapper.selectList(new EntityWrapper<PumpDO>().eq("gw_id", gwId));
        List<PumpDTO> pumpDTOList = new ArrayList<>();
        String key = "GW" + gwId;

        ValueOperations<String, DeviceOnlineBean> operations = redisTemplate.opsForValue();
//        operations.set("GW2", pumpDOList);
        //redis缓存中有，从缓存中获取
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {

            DeviceOnlineBean deviceOnline = operations.get(key);
            System.out.println("result == " + deviceOnline.toString());
//            Map<String, Object> dev = ObjectUtil.getKeyAndValue(deviceOnline);

            if (null == deviceOnline) {
                return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);//通过key获取的数据为空
            }

            for (int k = 0; k < pumpDOList.size(); k++) {//多个水泵的数据值
                PumpDTO pumpDTO = new PumpDTO();
                pumpDTO.setId(deviceOnline.getId());
                pumpDTO.setNumber(pumpDOList.get(k).getNumber());
//
//                //传感器数据
                List<ReportDataBean> reportDataBeanList = deviceOnline.getSensors();
                if(null == reportDataBeanList){
                    return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
                }
                if (reportDataBeanList.size() != 0) {//传感器有数值
                    for (int i = 0; i < reportDataBeanList.size(); i++) {
                        if (reportDataBeanList.get(i).getAddr().equals(pumpDOList.get(k).getVoltageAddr())
                                && reportDataBeanList.get(i).getType().equals("voltage")) {//电压传感器
                            List<Float> voltage = reportDataBeanList.get(i).getValue();
                            pumpDTO.setVDataValue1(voltage.get(0));
                            pumpDTO.setVDataValue2(voltage.get(1));
                            pumpDTO.setVDataValue3(voltage.get(2));
                        } else if (reportDataBeanList.get(i).getAddr().equals(pumpDOList.get(k).getElectricityAddr())
                                && reportDataBeanList.get(i).getType().equals("electricity")) {//电流传感器
                            List<Float> elec = reportDataBeanList.get(i).getValue();
                            pumpDTO.setEDataValue1(elec.get(0));
                            pumpDTO.setEDataValue2(elec.get(1));
                            pumpDTO.setEDataValue3(elec.get(2));
                        } else if (reportDataBeanList.get(i).getAddr().equals(pumpDOList.get(k).getRuntimesAddr())
                                && reportDataBeanList.get(i).getType().equals("runtimes")) {//
                            pumpDTO.setRuntimes(reportDataBeanList.get(i).getValue().get(0));
                        } else if (reportDataBeanList.get(i).getType().equals("liquidLevel")) {
                            DecimalFormat df = new DecimalFormat("#.##");
                            Float level = reportDataBeanList.get(i).getValue().get(0) / 1000;
                            Float waterLevel = Float.parseFloat(df.format(level));

                            //设置水位报警状态
                            GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(gwId));
                            if (gatewayDO != null) {
                                //低于设定水位, 无报警
                                if (gatewayDO.getWaterLevel() != null) {
                                    if (gatewayDO.getWaterLevel() - waterLevel > 0.00001f) {
                                        pumpDTO.setAlarm(0);
                                    } else {//高出设定水位,报警
                                        pumpDTO.setAlarm(1);
                                    }
                                }
                            }
                        }
                    }
                }
                //继电器数据
                List<RelayBean> relayBeanList = deviceOnline.getRelays();
                if (relayBeanList.size() != 0) {
                    for (int i = 0; i < relayBeanList.size(); i++) {
                        if ((relayBeanList.get(i).getAddr485().equals(pumpDOList.get(k).getRelayAddr())) && //寄存器485地址
                                (relayBeanList.get(i).getPosition().equals(pumpDOList.get(k).getRelayPositionRun() + 2))) {//寄存器第几位
                            //设置设备运行状态
                            char status = relayBeanList.get(i).getStatus();
                            pumpDTO.setStatus(status);

                        }
                    }
                }
                pumpDTOList.add(pumpDTO);
            }
        } else {
            return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);//缓冲中没有数据，网关不在线
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS, pumpDTOList);
    }

    /**
     * 控制水泵开关
     *
     * @param pumpControlDTO
     * @return
     */
    @Override
    public ObjectRestResponse<PumpDTO> updatePumpData(ControlDTO pumpControlDTO) {

        //被启用的用户具有控制权限
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDO userDO = userMapper.selectOne(UserDO.of().setUsername(username));
        System.out.println("gw===" + pumpControlDTO.getId() + "user===" + userDO.getId());
        GwUserDO gwUserDO = gwUserMapper.selectOne(GwUserDO.of().setGwId(pumpControlDTO.getId()).setUserId(userDO.getId()));
        //查找数据库中对应的控制
        PumpDO isExist = pumpMapper.selectOne(PumpDO.of().setGwId(pumpControlDTO.getId()).setNumber(pumpControlDTO.getNumber()));
        if (isExist == null) {
            return new ObjectRestResponse<>(ResultCode.DEVICE_NOT_EXIST);
        }

        if (null == gwUserDO) {
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        if ((null != gwUserDO) && (0 == gwUserDO.getUseFlag())) {
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("terminal", "web");
        map.put("msgType", "control");
        map.put("id", pumpControlDTO.getId());
        String run = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionRun();
        map.put("run", run);
        String stop = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionStop();
        map.put("stop", stop);
        if (pumpControlDTO.getStatus() == 1) {//
            String order = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionRun() + "#" + 1;
            map.put("order", order);
        } else {//01
            String order = "#" + isExist.getRelayAddr() + "#" + isExist.getRelayPositionStop() + "#" + 1;
            map.put("order", order);
        }
        log.info("水泵控制发送数据为" + map.toString());
        ObjectRestResponse<DeviceOnlineBean> sendControl = SendMessageUtil.sendQueryOrControl(map);
//        //发送成功，通过redis获取数据
        PumpDTO pumpDTO = new PumpDTO();
        pumpDTO.setNumber(pumpControlDTO.getNumber());
        pumpDTO.setId(pumpControlDTO.getId());
        if (sendControl.getCode().equals("00000")) {

            DeviceOnlineBean deviceOnline = sendControl.getData();
            System.out.println("result运行时间== " + deviceOnline.toString());
            log.info("接收数据成功,result===" + deviceOnline.toString());
            //传感器数据
            List<ReportDataBean> reportDataBeanList = deviceOnline.getSensors();
            if (reportDataBeanList.size() != 0) {//传感器有数值
                for (int i = 0; i < reportDataBeanList.size(); i++) {
                    if (reportDataBeanList.get(i).getAddr().equals(isExist.getVoltageAddr())
                            && reportDataBeanList.get(i).getType().equals("voltage")) {//电压传感器
                        List<Float> voltage = reportDataBeanList.get(i).getValue();
                        pumpDTO.setVDataValue1(voltage.get(0));
                        pumpDTO.setVDataValue2(voltage.get(1));
                        pumpDTO.setVDataValue3(voltage.get(2));
                    } else if (reportDataBeanList.get(i).getAddr().equals(isExist.getElectricityAddr())
                            && reportDataBeanList.get(i).getType().equals("electricity")) {//电流传感器
                        List<Float> elec = reportDataBeanList.get(i).getValue();
                        pumpDTO.setEDataValue1(elec.get(0));
                        pumpDTO.setEDataValue2(elec.get(1));
                        pumpDTO.setEDataValue3(elec.get(2));
                    } else if (reportDataBeanList.get(i).getAddr().equals(isExist.getRuntimesAddr())
                            && reportDataBeanList.get(i).getType().equals("runtimes")) {//
                        pumpDTO.setRuntimes(reportDataBeanList.get(i).getValue().get(0));
                    } else if (reportDataBeanList.get(i).getType().equals("liquidLevel")) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        Float level = reportDataBeanList.get(i).getValue().get(0) / 1000;
                        Float waterLevel = Float.parseFloat(df.format(level));
                        //设置水位报警状态
                        GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(pumpControlDTO.getId()));
                        if (gatewayDO != null) {
                            Float waterLevelSet = gatewayDO.getWaterLevel();
                            //低于设定水位, 无报警
                            if (waterLevelSet != null) {
                                if (waterLevelSet - waterLevel > 0.00001f) {
                                    pumpDTO.setAlarm(0);
                                } else {//高出设定水位,报警
                                    pumpDTO.setAlarm(1);
                                }
                            }

                        }
                    }
                }
            }
            //继电器数据
            List<RelayBean> relayBeanList = deviceOnline.getRelays();
            if (relayBeanList.size() != 0) {
                for (int i = 0; i < relayBeanList.size(); i++) {
                    if ((relayBeanList.get(i).getAddr485().equals(isExist.getRelayAddr()) && //寄存器485地址
                            (relayBeanList.get(i).getPosition().equals(isExist.getRelayPositionRun() + 2)))) {//寄存器第几位
                        //设置设备运行状态
                        char status = relayBeanList.get(i).getStatus();
                        pumpDTO.setStatus(status);

                    }
                }
            }

        } else {
            return new ObjectRestResponse<PumpDTO>(sendControl.getCode(), sendControl.getMessage(), null);
        }
        return new ObjectRestResponse<PumpDTO>(ResultCode.SUCCESS, pumpDTO);
    }
}
