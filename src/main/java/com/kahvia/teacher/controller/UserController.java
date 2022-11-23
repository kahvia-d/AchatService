package com.kahvia.teacher.controller;

import com.kahvia.teacher.dao.UserDao;
import com.kahvia.teacher.pojo.OnlineUser;
import com.kahvia.teacher.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserDao userDao;
    @PostMapping ("/check")
    OnlineUser userCheck(@RequestBody User user){
        List<User> users=userDao.checkUser( user.getUsername(), user.getPassword());
        if (users.size()!=0){
            OnlineUser onlineUser=new OnlineUser();
            onlineUser.setUserId(users.get(0).getId());
            onlineUser.setName(users.get(0).getShowName());
            onlineUser.setHeadImg(users.get(0).getHeadImg());
            return onlineUser;
        }
        return null;
    }


}
