package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;
import com.gleam.entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务端，在监听9999，等待客户端的连接，并保持通信
 */
public class Server {

    private ServerSocket ss = null;
    //创建一个集合，存放多个用户
    //ConcurrentHashMap处理线程安全
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {//在静态代码块初始化hash
        validUsers.put("Gleam",new User("Gleam","123"));
        validUsers.put("test",new User("test","123"));
        validUsers.put("admin",new User("admin","123"));
        validUsers.put("lw",new User("lw","123"));
        validUsers.put("root",new User("root","123"));
    }

    //验证用户是否有效
    private boolean checkUser(String userid, String pwd){
        User user = validUsers.get(userid);
        if(user == null){
            return false;
        }
        if(!user.getPassword().equals(pwd)){
            return false;
        }
        return true;
    }

    public Server(){
        try {
            System.out.println("服务器在9999端口监听");
            //端口号可以写在配置文件中
            ss = new ServerSocket(9999);
            //监听动作是持续的，所以用while循环
            while (true){
                Socket socket = ss.accept();
                //得到socket关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //读取客户端发送的User对象
                User user = (User) ois.readObject();
                //创建一个Message对象,用于回复客户端
                Message message = new Message();
                //进行数据库验证
                if(checkUser(user.getUserid(), user.getPassword())){
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通讯，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, user.getUserid());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把改线程对象放入到一个集合中进行管理
                    ManageClientThreads.addClientThread(user.getUserid(), serverConnectClientThread);
                }else { //验证失败
                    System.out.println("用户id=" + user.getUserid() + "登陆失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //如果服务端退出了while循环，不再监听，因此需要关闭serverSocket资源
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
