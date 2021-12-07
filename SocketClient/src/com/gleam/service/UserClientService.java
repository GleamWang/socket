package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;
import com.gleam.entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成
 * 用户登录注册
 * 等等功能
 */
public class UserClientService {

    private User user = new User();
    //因为socket在其他地方也可能使用，因此也做成属性
    private Socket socket;

    //根据userid和pwd，到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd){
        boolean b = false;
        //创建User对象
        user.setUserid(userId);
        user.setPassword(pwd);
        try {
            //连接到服务器，发送user对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"),9999);
            //得到OutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //发送user对象
            oos.writeObject(user);

            //读取从服务端回复的一个message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){ //登陆成功
                //创建一个和服务器端保持通信的现场->创建一个线程类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端的线程
                clientConnectServerThread.start();
                //线程需要一个集合来管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            }else {
                //登陆失败,不能启动和服务器通信的线程,关闭socket
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList(){
        //发送一个message,类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserid());
        //发送给服务器
        try {
            //得到当前userid对应的线程的Socket的OutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream
                    (ManageClientConnectServerThread.getClientConnectServerThread
                            (user.getUserid()).getSocket().getOutputStream());
            //向服务端请求在线用户列表
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //退出客户端，并给服务端发送一个退出系统的message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserid());//一定要指定是哪个客户端要退出
        //发送message
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(user.getUserid()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(user.getUserid() + "退出了系统");
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
