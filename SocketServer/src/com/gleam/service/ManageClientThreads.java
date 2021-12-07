package com.gleam.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 用于管理和客户端
 * 通讯的线程
 */
public class ManageClientThreads {

    //我们把多个线程放入到HashMap集合，key就是用户id，value就是线程
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    //将某个线程加入到集合中
    public static void addClientThread(String useId, ServerConnectClientThread serverConnectClientThread){
        hm.put(useId,serverConnectClientThread);
    }

    //通过userid，可以得到对应的线程
    public static ServerConnectClientThread getServerConnectClientThread(String userid){
        return hm.get(userid);
    }

    //返回在线用户列表
    public static String getOnlineUser(){
        //遍历集合,遍历hashMap的key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()){
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    //从集合中移除某个线程对象
    public static void removeServerConnectClientThread(String userid){
        hm.remove(userid);
    }
}
