package cn.com.tzy.springbootstartersmsbasic.model;

public class Param {
    public Integer id;
    //手机号
    public String mobile;
    //模板类型
    public int templateType;
    //发送内容
    public String content;
    //模板编号
    public String templateCode;
    //变量
    public String vairable;
    //验证码
    public String verificationCode;
    //redis缓存时长
    public int redisTime;
}
