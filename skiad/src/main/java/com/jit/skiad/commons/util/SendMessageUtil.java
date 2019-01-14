package com.jit.skiad.commons.util;

import com.jit.skiad.bean.DeviceOnlineBean;
import com.jit.skiad.bean.RecieveBean;
import com.jit.skiad.bean.RelayBean;
import com.jit.skiad.bean.ReportDataBean;
import com.jit.skiad.commons.ObjectRestResponse;
import com.jit.skiad.commons.ResultCode;
import com.jit.skiad.commons.client.ConnectMina;
import com.jit.skiad.commons.util.JacksonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMessageUtil {

    /**
     * 发送自动控制命令，并返回控制结果
     * @param controlOrder 控制命令MAP
     * @return
     * @throws IOException
     */
    public static ObjectRestResponse sendAutoControl(Map<String, Object> controlOrder) {
        ConnectMina client = new ConnectMina();
        try {
            client.connect();
        }catch (IOException e){
            return new ObjectRestResponse<>(ResultCode.MINA_SENDMESSAGE_ERROR);
        }
        if(client.sendMessage(controlOrder)){
            String re = client.recvieMessage();
            RecieveBean relay = JacksonUtils.readValue(re, RecieveBean.class);
            List<ReportDataBean> contents = relay.getContent();
            if("server".equals(relay.getTerminal())){
                // 网关不在线
                return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);
            }else{

            }
        }else{
            System.out.println("消息发送失败");
            return new ObjectRestResponse<>(ResultCode.MINA_SENDMESSAGE_ERROR);
        }
        client.disconnect();
        return new ObjectRestResponse<>(ResultCode.SUCCESS);
    }

    /**
     * 向mina服务器发送指令
     * @param queryOrder 指令（控制或查询指令）
     * @return
     * @throws IOException
     */
    public static ObjectRestResponse<DeviceOnlineBean> sendQueryOrControl(Map<String, Object> queryOrder){
        DeviceOnlineBean res = new DeviceOnlineBean();
        Integer id = (Integer) queryOrder.get("id");
        res.setId(id);
        ConnectMina client = new ConnectMina();
        try {
            client.connect();
        }catch (IOException e){
            client.disconnect();
            return new ObjectRestResponse<>(ResultCode.MINA_SENDMESSAGE_ERROR);
        }
        if(client.sendMessage(queryOrder)){
            String re = client.recvieMessage();
            RecieveBean relay = JacksonUtils.readValue(re, RecieveBean.class);
            if (relay == null){
                client.disconnect();
                return new ObjectRestResponse<>(ResultCode.MINA_RECEIVE_DATA_ERROR);
            }
            List<ReportDataBean> contents = relay.getContent();
            if("server".equals(relay.getTerminal())){
                client.disconnect();
                // 网关不在线
                return new ObjectRestResponse<>(ResultCode.GATEWAY_NOT_ON_LINE);
            }
            if(null != contents && contents.size() > 0){
                List<RelayBean> relays = new ArrayList<>();
                List<ReportDataBean> sensors = new ArrayList<>();
                for(ReportDataBean bean:contents){
                    if(bean.getType().equals("dma")){
                        Short value = bean.getValue().get(0).shortValue();
                        String s = Integer.toBinaryString(value);
                        int i;
                        int j = 0;
                        for(i = s.length()-1;i >= 0;i--){
                            relays.add(new RelayBean(bean.getAddr(),j,s.charAt(i)));
                            j++;
                        }

                        while(j < 8){
                            relays.add(new RelayBean(bean.getAddr(),j++,'0'));
                        }
                    } else{
                        sensors.add(bean);
                    }
                }
                res.setSensors(sensors);
                res.setRelays(relays);
            }
        }else{
            client.disconnect();
            System.out.println("消息发送失败");
            return new ObjectRestResponse<>(ResultCode.MINA_SENDMESSAGE_ERROR);
        }
        client.disconnect();
        return new ObjectRestResponse<>(ResultCode.SUCCESS,res);
    }
//    public static void main(String[] args) throws IOException {
//        ConnectMina client = new ConnectMina();
//        client.connect();
//        Map<String, Object> controlOrder = new HashMap<>();
//        controlOrder.put("id",2);
//        controlOrder.put("terminal","web");
//        controlOrder.put("msgType","control");
//        controlOrder.put("order","#254#1#0");
//        if(client.sendMessage(controlOrder)){
//            String re = client.recvieMessage();
//            System.out.println("============2============");
//            System.out.println(re);
//            RecieveBean relay = JacksonUtils.readValue(re, RecieveBean.class);
//            List<ReportDataBean> contents = relay.getContent();
//            if(null != contents && contents.size() > 0){
//               Integer value = contents.get(0).getValue().get(0).intValue();
//                String s = Integer.toBinaryString(value);
//                System.out.println(s);
//            }
//
//            client.disconnect();
//        }else{
//            System.out.println("消息发送失败");
//        }
//
//    }
}
