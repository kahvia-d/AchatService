package com.kahvia.teacher.controller;

import com.kahvia.teacher.dao.UserDao;
import com.kahvia.teacher.utils.QiFileTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    UserDao userDao;
    @PostMapping("/upload/{userId}")
    public String uploadImg(MultipartFile file, HttpServletRequest request,@PathVariable int userId){
        String url=QiFileTools.uploadFile(file,request,userId);
        if (url!=null)
            userDao.setUserHeadImg(userId,url);
        return url;
    }

    @PostMapping("/sendPic/{userId}")
    public String sendPic(MultipartFile file, HttpServletRequest request,@PathVariable int userId){
        String url=QiFileTools.uploadFile(file,request,userId);
        return url;
    }


}
