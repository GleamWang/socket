package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;

import java.io.*;

/**
 * 该类完成文件的传输
 */
public class FileClientService {

    public void sendFileToOne(String src, String dest, String sender, String getterId){
        //读取src文件 -> message对象
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(sender);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            //将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //提示信息
        System.out.println("\n" + message.getSender() + " 给 " +
                getterId + " 发送文件：" + src + " 到对方的电脑的目录 " + dest);
        //发送
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sender).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}