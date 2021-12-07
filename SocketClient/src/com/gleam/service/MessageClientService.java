package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 提供和消息相关的服务方法
 */
public class MessageClientService {

    public void sendMessageToOne(String content, String sender, String getterId){
        //构建message
        Message message = new Message();
        message.setSender(sender);
        message.setGetter(getterId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSendTime(new Date().toString());
        System.out.println(sender + " 对 " + getterId + " 说 " + content);
        //发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sender).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAll(String content, String sender){
        //构建message
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSendTime(new Date().toString());
        System.out.println(sender + " 对大家说 " + content);
        //发送给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sender).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
