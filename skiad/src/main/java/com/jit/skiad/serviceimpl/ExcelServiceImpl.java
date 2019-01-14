package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.commons.util.ExcelUtils;
import com.jit.skiad.domain.AlarmDO;
import com.jit.skiad.dto.WaterDTO;
import com.jit.skiad.mapper.AlarmMapper;
import com.jit.skiad.serviceinterface.ExcelService;
import com.jit.skiad.dto.ExcelData;

import com.jit.skiad.serviceinterface.WaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private AlarmMapper alarmMapper;

    @Autowired
    private WaterService waterService;
    @Override
    public void exportAlarmExcel(HttpServletResponse response,Integer gwId) throws Exception {
        List<AlarmDO> alarmDOS = alarmMapper.selectList(new EntityWrapper<AlarmDO>().eq("gw_id",gwId));
        ExcelData excelData=ExcelData.of();
        excelData = setAlarmList(alarmDOS);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sdf.format(new Date());
        ////下载测试
//        File f = new File("D:/"+"alarm"+fileName+".xlsx");
//        FileOutputStream out = new FileOutputStream(f);
//        ExcelUtils.exportExcel(excelData, out);
//        out.close();
        ExcelUtils.exportExcel(response,"alarm"+fileName+".xlsx",excelData);
    }

    private ExcelData setAlarmList(List<AlarmDO> alarmDOS){
        ExcelData excelData = ExcelData.of();
        excelData.setName("alarm");
        List<String> titles = new ArrayList<>();
        titles.add("序号");
        titles.add("原因");
        titles.add("操作员");
        titles.add("时间");
        titles.add("备注信息");

        excelData.setTitles(titles);
        List<List<Object>> rows = new ArrayList<>();
        Integer number = 1;
        for(AlarmDO alarm:alarmDOS){
            List<Object> row = new ArrayList();
            row.add(number);

            row.add(alarm.getReason());
            row.add(alarm.getOperator());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(alarm.getTime() !=null){
                row.add(sdf.format(alarm.getTime()));
            }else {
                row.add("");
            }

            row.add(alarm.getRemark());
            rows.add(row);
            number++;
        }

        excelData.setRows(rows);
        return excelData;
    }

    @Override
    public void exportWaterHistoryExcel(HttpServletResponse response, Integer gwId, String startTime, String endTime) throws Exception {
       WaterDTO waterDTO = waterService.getHistory(gwId,startTime,endTime);

               ExcelData excelData=ExcelData.of();
        excelData = setWaterList(waterDTO);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = sdf.format(new Date());
        ////下载测试
//        File f = new File("D:/"+"water"+fileName+".xlsx");
//        FileOutputStream out = new FileOutputStream(f);
//        ExcelUtils.exportExcel(excelData, out);
//        out.close();
        ExcelUtils.exportExcel(response,"water"+fileName+".xlsx",excelData);
    }
    private ExcelData setWaterList(WaterDTO waterDTO){
        ExcelData excelData = ExcelData.of();
        excelData.setName("WaterHistory");
        List<String> titles = new ArrayList<>();
        titles.add("序号");
        titles.add("时间");
        titles.add("水位");


        excelData.setTitles(titles);
        List<List<Object>> rows = new ArrayList<>();
        Integer number = 1;
        Map<String,List> maps = waterDTO.getRes();
        List<Float> datas = maps.get("dataList");
        List<String> times = maps.get("timeList");

        if(times.size()!=0) {

            for (int i = 0; i < times.size(); i++) {
                List<Object> row = new ArrayList();
                row.add(number);
                row.add(times.get(i));
                row.add(datas.get(i));
                rows.add(row);
                number++;
            }
        }
        excelData.setRows(rows);
        return excelData;
    }

}
