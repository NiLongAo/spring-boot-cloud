package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideocore.demo.SipTransactionInfo;
import lombok.extern.log4j.Log4j2;


/**
 * 注册相关信息
 */
@Log4j2
public class SipTransactionManager {
    private String SIP_TRANSACTION =  "SIP_TRANSACTION:";

    private String DEVICE_PREFIX = String.format("%s%s",VideoConstant.DEVICE_PREFIX,SIP_TRANSACTION);

    private String PARENT_PLATFORM_PREFIX =  String.format("%s%s",VideoConstant.PARENT_PLATFORM_PREFIX,SIP_TRANSACTION);


    public void putDevice(String deviceId, SipTransactionInfo sipTransactionInfo){
        String key = String.format("%s%s", DEVICE_PREFIX, deviceId);
        RedisUtils.set(key,sipTransactionInfo);
    }

    public SipTransactionInfo findDevice(String deviceId){
        String key = String.format("%s%s", DEVICE_PREFIX, deviceId);
        return (SipTransactionInfo) RedisUtils.get(key);
    }

    public void putParentPlatform(String parentPlatformId, SipTransactionInfo sipTransactionInfo){
        String key = String.format("%s%s", PARENT_PLATFORM_PREFIX, parentPlatformId);
        RedisUtils.set(key,sipTransactionInfo);
    }

    public SipTransactionInfo findParentPlatform(String parentPlatformId){
        String key = String.format("%s%s", PARENT_PLATFORM_PREFIX, parentPlatformId);
        return (SipTransactionInfo) RedisUtils.get(key);
    }
}
