package cn.com.tzy.springbootstartersmscore.config;


import cn.com.tzy.springbootstartersmsbasic.demo.SendModel;
import cn.com.tzy.springbootstartersmsbasic.model.Result;

public interface SmsHttpClientManager {
    public Result send(SendModel param);
}
