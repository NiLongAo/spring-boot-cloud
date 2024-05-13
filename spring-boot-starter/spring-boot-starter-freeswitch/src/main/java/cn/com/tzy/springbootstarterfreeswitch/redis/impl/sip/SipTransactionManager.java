package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SipTransactionInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


/**
 * 注册相关信息
 */
@Log4j2
@Component
public class SipTransactionManager {
    private String SIP_TRANSACTION =  "SIP_TRANSACTION:";

    private String DEVICE_PREFIX = String.format("%s%s", SipConstant.DEVICE_PREFIX,SIP_TRANSACTION);

    private String PARENT_PLATFORM_PREFIX =  String.format("%s%s",SipConstant.PARENT_PLATFORM_PREFIX,SIP_TRANSACTION);


    public void putDevice(String agentCode, SipTransactionInfo sipTransactionInfo){
        String key = String.format("%s%s", DEVICE_PREFIX, agentCode);
        RedisUtils.set(key,sipTransactionInfo);
    }

    public SipTransactionInfo findDevice(String agentCode){
        String key = String.format("%s%s", DEVICE_PREFIX, agentCode);
        return (SipTransactionInfo) RedisUtils.get(key);
    }

    public void putParentPlatform(String agentCode, SipTransactionInfo sipTransactionInfo){
        String key = String.format("%s%s", PARENT_PLATFORM_PREFIX, agentCode);
        RedisUtils.set(key,sipTransactionInfo);
    }

    public SipTransactionInfo findParentPlatform(String agentCode){
        String key = String.format("%s%s", PARENT_PLATFORM_PREFIX, agentCode);
        return (SipTransactionInfo) RedisUtils.get(key);
    }
}
