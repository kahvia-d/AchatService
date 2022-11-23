package com.kahvia.teacher.wst;

import com.alibaba.fastjson.JSON;
import com.kahvia.teacher.dao.UserDao;
import com.kahvia.teacher.pojo.Message;
import com.kahvia.teacher.pojo.OnlineUser;
import com.kahvia.teacher.pojo.Temp;
import com.kahvia.teacher.pojo.User;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import sun.rmi.runtime.Log;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/achat/{userId}/{name}")
@Component
public class MyWebSocket {
    private static UserDao userDao;
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static Map<String,Session> webSocketSet = new ConcurrentHashMap<>();

    private static Map<String,OnlineUser> userSet=new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String userId;

    @Autowired
    public void setUserDao(UserDao userDao){
        MyWebSocket.userDao=userDao;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId,@PathParam("name") String name) {
        this.session = session;
        this.userId=userId;
        webSocketSet.put(userId,session);     //加入set中
        addOnlineCount();           //在线数加1
        setUserOnline();
        User user= userDao.getUserById(Integer.parseInt(userId)).get(0);
        OnlineUser onlineUser=new OnlineUser(Integer.parseInt(userId),user.getShowName(),user.getHeadImg());
        userSet.put(userId,onlineUser);
        System.out.println(JSON.toJSONString(userSet));
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        updateOnlineList();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        setUserOffline();
        webSocketSet.remove(this);  //从set中删除
        userSet.remove(this);
        updateOnlineList();
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("来自客户端的消息:" + message);
        Message msg=JSON.parseObject(message, Message.class);
//        userDao.sendMessage();
        System.out.println(msg.getUserId()+"发送消息给："+msg.getToUserId()+",内容是："+msg.getContent());

        Temp temp=new Temp();
        temp.setMsg(message);

        webSocketSet.get(String.valueOf(msg.getUserId())).getBasicRemote().sendText(JSON.toJSONString(temp));
        webSocketSet.get(String.valueOf(msg.getToUserId())).getBasicRemote().sendText(JSON.toJSONString(temp));
    }


    @OnError
    public void onError(Session session, Throwable error) {
        for (String key: webSocketSet.keySet()){
            if (webSocketSet.get(key)==session){
                webSocketSet.remove(key);
                userSet.remove(key);
                return;
            }
        }
        updateOnlineList();
        subOnlineCount();
        System.out.println("有连接发生错误");
        System.out.println("当前在线人数:"+onlineCount);
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static void sendInfoToAll(String message) throws IOException {
        for (Session session1 : webSocketSet.values()) {
            try {
                session1.getBasicRemote().sendText(message);
            } catch (IOException e) {
                continue;
            }
        }
    }



    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    public static synchronized void updateOnlineList(){
        Temp temp = new Temp();
        temp.setMessageType(1);//1代表是由服务端推送的消息，也就是在线的人列表
        temp.setMsg(JSON.toJSONString(userSet));
        for (Session session1 : webSocketSet.values()) {
            try {
                session1.getBasicRemote().sendText(JSON.toJSONString(temp));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setUserOnline(){
        userDao.setUserStatus(Integer.parseInt(userId),1);
    }
    void setUserOffline(){
        userDao.setUserStatus(Integer.parseInt(userId),0);
    }

}