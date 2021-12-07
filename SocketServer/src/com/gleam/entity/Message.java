package com.gleam.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sender; //发送者
    private String getter; //接收者
    private String content; //发送内容
    private String sendTime; //发送时间
    private String mesType; //消息类型[可以在接口中定义消息类型]

    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest;
    private String src;

}
