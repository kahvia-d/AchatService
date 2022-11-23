package com.kahvia.teacher.pojo;

public class Temp {
    int messageType=0;//0为用户发送信息，1为客户端推送的在线信息
    String msg;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
