package cn.com.tzy.springbootstartersmsbasic.model;

public class Result {
    public static final Result FAIL = new Result();

    public int id; //对应的数据库ID
    public int httpCode; //http响应码
    public String httpContent; //http响应内容
    public boolean success;
    public String msgId;
    public String message;

    public int type;
    public Integer templateType;
    public String content;
    public String templateCode;
    public String vairable;
    //验证码
    public String verificationCode;
    //redis缓存时长
    public int redisTime;



}
