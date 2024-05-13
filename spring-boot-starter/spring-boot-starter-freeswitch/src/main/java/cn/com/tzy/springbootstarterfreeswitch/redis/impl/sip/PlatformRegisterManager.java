package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.PlatformRegisterInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@Component
public class PlatformRegisterManager {

    @Resource
    private VideoProperties videoProperties;

    private String PLATFORM_REGISTER_CATCH_PREFIX = SipConstant.PLATFORM_REGISTER_CATCH_PREFIX;


    public void updatePlatformRegisterInfo(String callId, PlatformRegisterInfo platformRegisterInfo) {
        String key = PLATFORM_REGISTER_CATCH_PREFIX + videoProperties.getServerId() +  ":" + callId;
        RedisUtils.set(key, platformRegisterInfo, 30);
    }


    public PlatformRegisterInfo queryPlatformRegisterInfo(String callId) {
        Object o = RedisUtils.get(PLATFORM_REGISTER_CATCH_PREFIX + videoProperties.getServerId() + ":" + callId);
        return (PlatformRegisterInfo)o;
    }

    public void delPlatformRegisterInfo(String callId) {
        RedisUtils.del(PLATFORM_REGISTER_CATCH_PREFIX + videoProperties.getServerId() +  ":" + callId);
    }

    public void cleanPlatformRegisterInfos() {
        List regInfos = RedisUtils.keys(PLATFORM_REGISTER_CATCH_PREFIX + videoProperties.getServerId() +  ":" + "*");
        for (Object key : regInfos) {
            RedisUtils.del(key.toString());
        }
    }

}
