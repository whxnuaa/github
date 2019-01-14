package com.jit.skiad.commons.client;

import com.jit.skiad.commons.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class ConnectMina {

    private final static String IP = "223.2.197.246";// 连接服务器的IP
    private final static Integer PORT = 8888;// 连接服务器的端口
//    private final static Integer SOTIMEOUT = 30 * 1000;// 超时时间，以毫秒为单位
    private  Socket socket;
    private  OutputStream outStr = null;
    private  InputStream inStr = null;
//    private static Thread tKeep = new Thread(new KeepThread());

    public void connect() throws IOException{
        if(null == socket || socket.isClosed()){
            InetAddress address = InetAddress.getByName(IP);
            socket = new Socket(address, PORT);
//            tKeep.start();
            log.info("建立连接成功：" + IP + ":" + PORT);
        }
        outStr = socket.getOutputStream();
        inStr = socket.getInputStream();
    }

    public void disconnect(){
        try {
            outStr.close();
            inStr.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(Object message) {
        String s = JacksonUtils.toJson(message) + "\r\n";
        try {
                outStr.write(s.getBytes());
                outStr.flush();
                System.out.println("s1 :" + s);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
        }
    }

    public String recvieMessage(){
        try {
            System.out.println("==============开始接收数据===============");
            long t1 = System.currentTimeMillis();
            while (true) {
                byte[] b = new byte[1024];
                int r = inStr.read(b);
                if(r>-1){
                    String rs = new String(b);
//                    System.out.println( "================1=================" );
                    System.out.println( rs );
                    return rs;
                }
                long t2 = System.currentTimeMillis();
                if(t2-t1 > 15*1000){
                    //若15秒还未收到返回，则退出
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private static class KeepThread implements Runnable {
//        public void run() {
//            try {
//                System.out.println("=====================开始发送心跳包==============");
//
//                while (true) {
//                    try {
//                        Thread.sleep(15000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("发送心跳数据包");
//      				outStr.write("pong\n".getBytes());
//      				outStr.flush();
//                }
//            } catch (IOException e) {
//                log.info("连接断开。。。");
//            }
//
//        }
//    }


//	public static void main(String[] args) {
//		ConnectMina client = new ConnectMina();
//
//		List<String> zones = new ArrayList<>();
//		zones.add("4,塘口1,3");
//		GateWay gwmap = new GateWay(zones, "网关1", 300);
//		Property property = new Property(gwmap, null);
//		MessageVo update = new MessageVo("update", "网关1", 111, "web");
//		update.setProperty(property);
////
//		String s = JacksonUtils.toJson(update) + "\r\n";
//		System.out.println("s :" + s);
//		boolean res = client.sendMessage(update);
//		System.out.println("res:" + res);
//
//	}

}
