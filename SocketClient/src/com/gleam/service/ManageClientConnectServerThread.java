package com.gleam.service;

import java.util.HashMap;

/**
 * 管理客户端连接到服务器端
 * 的线程的类
 */
public class ManageClientConnectServerThread {
    //我们把多个线程放入到HashMap集合，key就是用户id，value就是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合中
    public static void addClientConnectServerThread(String useId, ClientConnectServerThread clientConnectServerThread){
        hm.put(useId,clientConnectServerThread);
    }

    //通过userid，可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userid){
        return hm.get(userid);
    }

}
