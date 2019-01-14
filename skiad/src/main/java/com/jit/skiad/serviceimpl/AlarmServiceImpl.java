package com.jit.skiad.serviceimpl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.domain.AlarmDO;
import com.jit.skiad.domain.GatewayDO;
import com.jit.skiad.domain.GwUserDO;
import com.jit.skiad.domain.UserDO;
import com.jit.skiad.mapper.AlarmMapper;
import com.jit.skiad.mapper.GatewayMapper;
import com.jit.skiad.mapper.GwUserMapper;
import com.jit.skiad.mapper.UserMapper;
import com.jit.skiad.serviceinterface.AlarmService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private GwUserMapper gwUserMapper;
    @Autowired
    private AlarmMapper alarmMapper;

    @Override
    public ObjectRestResponse<Void> getPumpAlarm(Integer gwId) {
        return null;
    }

    /**
     * 根据网关id获取当前水位
     * @param gwId
     * @return
     */
    @Override
    public ObjectRestResponse<GatewayDO> getWaterLevel(Integer gwId) {
        GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(gwId));
        if (gatewayDO == null) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE,null);
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS,gatewayDO);
    }

    /**
     * 更新水位
     * @param gwId 网关id
     * @param waterLevel 水位
     * @return
     */
    @Override
    public ObjectRestResponse<GatewayDO> updateWaterLevel(Integer gwId, Float waterLevel) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDO userDO = userMapper.selectOne(UserDO.of().setUsername(username));
        GwUserDO gwUserDO = gwUserMapper.selectOne(GwUserDO.of().setGwId(gwId).setUserId(userDO.getId()));
        if (gwUserDO == null){
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE);
        }
        GatewayDO gatewayDO = gatewayMapper.selectOne(GatewayDO.of().setId(gwId));
        //当前用户与网关无对应关系
        if (null == gatewayDO){
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        //保证gw_user表与gateway表同步,并设置新增用户权限为启用
        if (gwUserDO == null && gatewayDO != null){
            gwUserDO.setGwId(gwId).setUserId(userDO.getId()).setUseFlag(1);
            gwUserMapper.updateById(gwUserDO);
        }
        //用户的权限被禁用或未填写，返回无权限
        if ((gwUserDO !=null && gwUserDO.getUseFlag() == 0)){
            return new ObjectRestResponse<>(ResultCode.PERMISSION_NO_ACCESS);
        }
        //更新gateway表的水位
        gatewayDO.setOperator(username);
        gatewayDO.setWaterLevel(waterLevel);
        int flag = gatewayMapper.updateById(gatewayDO);
        if (flag>0){
            return new ObjectRestResponse<>(ResultCode.SUCCESS,gatewayDO);
        }else {
            return new ObjectRestResponse<>(ResultCode.DATA_IS_WRONG);
        }
    }

    /**
     * 获取所有预警数量
     * @return
     */
    @Override
    public ObjectRestResponse<Integer> getAlarmNumber() {
        List<GatewayDO> gatewayDOList = gatewayMapper.selectList(null);
        if (gatewayDOList.size()==0) {
            return new ObjectRestResponse<>(ResultCode.RESULE_DATA_NONE,null);
        }
        int total = 0;
        for (int i=0;i<gatewayDOList.size();i++){
            List<AlarmDO> alarmDOList = alarmMapper.selectList(new EntityWrapper<AlarmDO>().eq("gw_id",gatewayDOList.get(i).getId()));
            total+=alarmDOList.size();
        }
        return new ObjectRestResponse<>(ResultCode.SUCCESS,total);
    }

    /**
     * 根据网关id获取报警list
     * @param gwId
     * @return
     */
    @Override
    public ObjectRestResponse<List<AlarmDO>> getAlarmList(Integer gwId) {
        List<AlarmDO> alarmDOList = alarmMapper.selectList(new EntityWrapper<AlarmDO>().eq("gw_id",gwId));
        return new ObjectRestResponse<List<AlarmDO>>(ResultCode.SUCCESS,alarmDOList);
    }
}
