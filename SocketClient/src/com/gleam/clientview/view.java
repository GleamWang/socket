package com.gleam.clientview;

import com.gleam.service.FileClientService;
import com.gleam.service.MessageClientService;
import com.gleam.service.UserClientService;
import com.gleam.utils.Utility;

/**
 * 客户端的菜单页面
 */
public class view {

    private boolean loop = true; //控制是否显示菜单
    private String key = ""; //用于接收用户键盘输入
    private UserClientService userClientService = new UserClientService(); //用于登录服务器
    private MessageClientService messageClientService = new MessageClientService();//对象用户私聊/群聊
    private FileClientService fileClientService = new FileClientService();//该对象用于传输文件

    public static void main(String[] args) {
        new view().mainMenu();
        System.out.println("客服端退出系统. . .");
    }

    //显示主菜单
    private void mainMenu(){

        while (loop){
            System.out.println("=====欢迎登录网络通信系统=====");
            System.out.println("\t\t1 登陆系统");
            System.out.println("\t\t9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);

            //根据用户输入来处理不同的逻辑
            switch (key){
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密码：");
                    String pwd = Utility.readString(50);

                    //验证成功
                    if(userClientService.checkUser(userId,pwd)){
                        System.out.println("欢迎" + userId + "登陆成功！");
                        //进入到二级菜单
                        while (loop){
                            System.out.println("\n=====网络通信系统二级菜单(用户 " + userId + " )=====");
                            System.out.println("\t\t1 显示在线用户列表");
                            System.out.println("\t\t2 群发消息");
                            System.out.println("\t\t3 私聊消息");
                            System.out.println("\t\t4 发送文件");
                            System.out.println("\t\t9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入想对大家说的话：");
                                    String s = Utility.readString(100);
                                    messageClientService.sendMessageToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线)：");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    break;
                                case "4":
                                    System.out.print("请输入你想发送文件的用户(在线用户)：");
                                    getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径(形式如:d:\\xx.jpg)");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入把文件发送到对方的路径(形式如:d:\\yy.jpg)");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src, dest, userId, getterId);
                                    break;
                                case "9":
                                    //调用方法，给服务器发送一个退出系统的message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    }else { //登录服务器失败
                        System.out.println("=====登陆失败=====");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }

    }

}
