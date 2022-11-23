package com.kahvia.teacher.utils;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;

public class QiFileTools {
    public static String uploadFile(MultipartFile file,HttpServletRequest request,int userId){
        String path=request.getServletContext().getRealPath("");//获取当前servlet上下文的绝对路径
        File temp=new File(path,file.getOriginalFilename());//在这个路径下新建一个临时文件temp
        try {//接受用户上传的文件，往temp中输出
            InputStream inputStream=file.getInputStream();
            FileOutputStream fileOutputStream=new FileOutputStream(temp);
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
            int n=0;
            byte b[] = new byte[1024];//1024个字节，也就1024byte，即1kb
            while ((n=inputStream.read(b))!=-1)//read(b)，是说从输入流中读取“b的大小”这么多的数据到b中，并返回读取的字节个数，-1代表读取完了
            {
                bufferedOutputStream.write(b);//把读取到的的，存放在b中的数据写入到输出流指向的文件中
            }
            bufferedOutputStream.flush();//刷新缓存区，刷新后，缓冲输出流指向的底层输入流，即fileOutputStream会立即将缓存的内容写入目的地
            bufferedOutputStream.close();//先关闭上层输出流
            inputStream.close();//再关闭底层输出流。按理说关了上层，底层也会自动关。
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //方法一：自定义http请求，访问第三方图床的上传接口，并获取返回值
//        //定义一个空的map，用来存储请求第三方接口所需的数据
//        MultiValueMap<String,Object> map= new LinkedMultiValueMap<>();
//        //利用刚刚生成的临时文件，创建文件系统资源
//        FileSystemResource fileSystemResource=new FileSystemResource(temp);
//        //添加到数据map中，第三方所需的参数名为image
//        map.add("image",fileSystemResource);
//
//        //设置请求头，包括访问的浏览器，和请求体内容固定类型，传输文件要选择表单数据"multipart/form-data"
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
//        headers.add("Content-Type","multipart/form-data");
//
//        //生成http请求
//        HttpEntity<MultiValueMap<String, Object>> entity=new HttpEntity<>( map,headers);
//        //发送http请求，返回目标对象。返回的json数据会自动封装为目标对象。下面的imgtp免费图床跑路了。所以我换了七牛云，自己搭了一个。
//        UploadResult uploadResult= restTemplate.postForObject("https://imgtp.com/api/upload",entity,UploadResult.class);

        //方法二：使用七牛云官方的java sdk工具包，进行文件上传
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "你的accessKey";
        String secretKey = "你的secretKey";
        String bucket = "bucket-name";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = temp.getAbsolutePath();
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "achat/"+userId+"/"+new Date().getTime();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            temp.delete();//删除中转文件
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            if (response.statusCode==200)
                return "https://xxxxxx.cn/"+key;//这个我返回的图片链接，作为消息中网络图片源
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

        return null;
    }
}
