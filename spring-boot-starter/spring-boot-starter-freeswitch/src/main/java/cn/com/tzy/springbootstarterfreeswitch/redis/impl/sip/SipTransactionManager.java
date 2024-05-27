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


    public void putDevice(String agentKey, SipTransactionInfo sipTransactionInfo){
        RedisUtils.set(getKey(DEVICE_PREFIX, agentKey),sipTransactionInfo);
    }

    public SipTransactionInfo findDevice(String agentKey){
        return (SipTransactionInfo) RedisUtils.get(getKey(DEVICE_PREFIX, agentKey));
    }
    public void delDevice(String agentKey){
        RedisUtils.del(getKey(DEVICE_PREFIX,agentKey));
    }

    public void putParentPlatform(String agentKey, SipTransactionInfo sipTransactionInfo){
        RedisUtils.set(getKey(PARENT_PLATFORM_PREFIX, agentKey),sipTransactionInfo);
    }

    public SipTransactionInfo findParentPlatform(String agentKey){
        return (SipTransactionInfo) RedisUtils.get(getKey(PARENT_PLATFORM_PREFIX,agentKey));
    }

    public void delParentPlatform(String agentKey){
        RedisUtils.del(getKey(PARENT_PLATFORM_PREFIX,agentKey));
    }

    private String getKey(String prefix,String key) {
        return String.format("%s%s", prefix, key);
    }



}
