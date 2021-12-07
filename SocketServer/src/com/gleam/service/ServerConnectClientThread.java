package com.gleam.service;

import com.gleam.entity.Message;
import com.gleam.entity.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类的一个对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread{

    //服务器端有多个socket
    private Socket socket;
    //连接到服务端的用户id
    private String userid;

    public ServerConnectClientThread(Socket socket, String userid) {
        this.socket = socket;
        this.userid = userid;
    }

    public Socket getSocket(){
        return socket;
    }

    @Override
    public void run() { //线程处于一个运行的状态，可以发送/接收消息
        while (true){
            try {
                System.out.println("服务端和客户端" + userid + "保持通信，读取数据....");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //根据MESSAGE类型，做相应的业务处理
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    //客户端请求在线用户列表
                    System.out.println(message.getSender() + "正在请求在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回在线用户列表,构建一个message对象返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //根据message获取getterId,然后得到对应的线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    //得到对应socket的对象输出流，转发给指令客服
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);//转发,如果客户不在线，可以保存到数据库
                }else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //需要遍历管理线程的集合，把所有的线程的socket都得到，然后把message进行转发
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){
                        //取出在线用户的id
                        String onlineUserid = iterator.next().toString();
                        if(!onlineUserid.equals(message.getSender())){ //排除自己
                            //进行转发message
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserid).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    //根据getterId获取到对应的线程，将message对象转发
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    //转发
                    oos.writeObject(message);
                }else if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){//客户端退出
                    System.out.println(message.getSender() + "退出客户端");
                    //将这个客户端对应的线程从集合中删除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    //关闭连接
                    socket.close();
                    //退出线程
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
