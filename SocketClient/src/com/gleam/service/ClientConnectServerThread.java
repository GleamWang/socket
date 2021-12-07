package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    //该线程需要持有socket
    private Socket socket;

    //构造器可以接收一个socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    //为了更方便得到socket
    public Socket getSocket(){
        return socket;
    }

    @Override
    public void run() {
        //因为thread需要在后台保持和服务器端的通信，因此用一个while循环来控制
        while (true){
            try {
                System.out.println("客户端线程等待读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送数据，线程会阻塞在readObject方法
                Message message = (Message) ois.readObject();
                //判断message类型，然后做相应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线列表信息并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n=====当前在线用户列表=====");
                    for(String onlineUser:onlineUsers){
                        System.out.println("用户:" + onlineUser);
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //把服务器端转发的消息显示到控制台即可
                    System.out.println("\n" + message.getSendTime() + " " + message.getSender() + " 对 " + message.getGetter() + " 说；");
                    System.out.println(message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //显示在客户端的控制台即可
                    System.out.println("\n" + message.getSender() + "对大家说：" + message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    //如果是含有文件的消息
                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter()
                     + " 发文件： " + message.getSrc() + " 到我的电脑的目录 ： " + message.getDest());
                    //取出message的字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n保存文件成功~");
                }
                else {
                    System.out.println("是其他类型的数据");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
