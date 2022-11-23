package com.kahvia.teacher.pojo;

public class UploadResult {
    int code=200;//200为成功，500为失败
    String msg="success";//提示信息
    int time;//响应时间戳
    UploadResultData data;//相关数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public UploadResultData getData() {
        return data;
    }

    public void setData(UploadResultData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UploadResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", time=" + time +
                ", data=" + data.toString() +
                '}';
    }
}
