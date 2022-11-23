package com.kahvia.teacher.pojo;

public class OnlineUser {
    int userId;
    String name;

    String headImg;

    public OnlineUser(){}
    public OnlineUser(int userId,String name,String headImg){
        this.userId=userId;
        this.name=name;
        this.headImg=headImg;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
