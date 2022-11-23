package com.kahvia.teacher.dao;

import com.kahvia.teacher.pojo.Message;
import com.kahvia.teacher.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    //登录时检查用户名密码是否正确，正确则返回对应的用户信息
    @Select("select * from user where username=#{username} and password=#{password}")
//    List<User> checkUser(User user);
    List<User> checkUser(@Param("username") String name,@Param("password") String pwd);

    @Select("select * from user where id=#{userId}")
    List<User> getUserById(int userId);

    @Update("update user set headImg=#{headImg} where id=#{userId}")
    void setUserHeadImg(@Param("userId") int userId,@Param("headImg") String headImg);

    //set user status
    @Update("update user set status=#{status} where id=#{userId}")
    void setUserStatus(@Param("userId") int userId,@Param("status") int status);

    //send message
    @Insert("insert into messages values (fromUserId,toUserId,contentType,content)")
    void sendMessage(Message message);
}
