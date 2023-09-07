package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.sip.PlatformRegisterInfo;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
public class PlatformRegisterManager {

    @Resource
    private VideoProperties videoProperties;

    private String PLATFORM_REGISTER_CATCH_PREFIX = VideoConstant.PLATFORM_REGISTER_CATCH_PREFIX;


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
