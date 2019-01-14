package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.bean.DeviceOnlineBean;
import com.jit.skiad.bean.ReportDataBean;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.domain.CommondataDO;

import com.jit.skiad.domain.GatewayDO;
import com.jit.skiad.dto.WaterDTO;
import com.jit.skiad.mapper.DataMapper;
import com.jit.skiad.mapper.GatewayMapper;
import com.jit.skiad.serviceinterface.WaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class WaterServiceImpl implements WaterService {
    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GatewayMapper gatewayMapper;
    /**
     * 获取水位变化曲线数据list
     * @param gwId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public ObjectRestResponse<WaterDTO> getDataByTime(Integer gwId, String startTime, String endTime) {
        WaterDTO waterDTO = getHistory(gwId,startTime,endTime);
        return new ObjectRestResponse<WaterDTO>(ResultCode.SUCCESS,waterDTO);
    }
    @Override
    public WaterDTO getHistory(Integer gwId, String startTime, String endTime){
        //从db中获取
        List<CommondataDO> doList = dataMapper.selectList(new EntityWrapper<CommondataDO>()
                .between("report_time",startTime,endTime)
                .eq("gw_id",gwId).eq("sensor_type","liquidLevel"));

        Map<String, List> res = new HashMap<>(16);
        if ((null != doList && doList.size() > 0)){
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, List> resTemp = new HashMap<>(16);
            List<String> timeList = new ArrayList<>();
//

            Date first = doList.get(0).getReportTime();
            Date now = first;
            Date last = first;
            timeList.add(dateformat.format(now));

            //临时数据表
            List<Float> dataList = new ArrayList<>();
//
            for(CommondataDO data : doList) {
                now = data.getReportTime();
                List<Float> dataTemp = dataList;
                if(null == dataTemp) continue;
                if(now.getTime() - last.getTime() > 0) {
                    Long mins = (now.getTime() - last.getTime())/(60*1000);
                    Long timeSpan = mins/5;//五分钟间隔
                    Long remainder = mins%5;//余数
                    if(0 == remainder) {
                        timeSpan -= 1;
                    }
                    for(int i = 0;i < timeSpan;i++) {
                        timeList.add(dateformat.format(last.getTime() + 5*60*1000*(i+1)));
                    }
                    timeList.add(dateformat.format(now));
                    last = now;
                }
                int timeSize = timeList.size();
                int dataSize = dataTemp.size();
                if(dataSize+1 < timeSize) {
                    for(int j = dataSize+1;j < timeSize;j++) {
                        dataTemp.add(null);
                    }
                }
                if(-9999 == data.getDataValue()) {
                    dataTemp.add(null);
                } else {
                    Float nLevel = data.getDataValue()/1000;
                    DecimalFormat df = new DecimalFormat("#.##");
                    Float cLevel = Float.parseFloat(df.format(nLevel));
                    dataTemp.add(cLevel);
                }

            }
            res.put("dataList", dataList);
            res.put("timeList", timeList);
            System.out.println("datalist size===" + dataList.size() + "timelist size===" + timeList.size());
        }
        WaterDTO waterDTO = new WaterDTO();
        waterDTO.setGwId(gwId);
        waterDTO.setRes(res);
        return waterDTO;
    }

    /**
     * 获取当前水位和预警状态
     * @param gwId 网关id
     * @return
     */
    @Override
    public ObjectRestResponse<WaterDTO> getCurrentData(Integer gwId) {

        GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(gwId));
        Float waterLevelSet = gatewayDO.getWaterLevel();
        Float waterLevel = 0.0f;
        WaterDTO waterDTO = new WaterDTO();

        String key= "GW"+gwId;
        ValueOperations<String,DeviceOnlineBean> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            DeviceOnlineBean deviceOnline = operations.get(key);
            if (null == deviceOnline) {
                return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);//通过key获取的数据为空
            }

            //传感器数据
            List<ReportDataBean> reportDataBeanList = deviceOnline.getSensors();
            if (reportDataBeanList.size() != 0){//传感器有数值
                for (int i=0; i<reportDataBeanList.size(); i++){
                    if (reportDataBeanList.get(i).getType().equals("liquidLevel")){//水位
                       waterLevel = reportDataBeanList.get(i).getValue().get(0);
                    }
                }
            }
        }
        Float nLevel = waterLevel/1000;
        DecimalFormat df = new DecimalFormat("#.##");
        Float cLevel = Float.parseFloat(df.format(nLevel));
        //低于设定水位, 无报警
        if (waterLevelSet-nLevel>0.00001f){
            waterDTO.setAlarm(0);
        }else {//高出设定水位,报警
            waterDTO.setAlarm(1);
        }
        waterDTO.setWaterLevel(cLevel);
        waterDTO.setGwId(gwId);
        return new ObjectRestResponse<WaterDTO>(ResultCode.SUCCESS,waterDTO);
    }
}
