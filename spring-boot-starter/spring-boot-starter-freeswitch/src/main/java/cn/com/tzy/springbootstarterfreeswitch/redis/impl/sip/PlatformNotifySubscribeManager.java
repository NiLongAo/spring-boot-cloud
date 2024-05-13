package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.NotifySubscribeInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

/**
 * 上级平台订阅缓存
 * redis 订阅方式
 */
@Component
public class PlatformNotifySubscribeManager {
    //报警订阅缓存key
    private static final String VIDEO_PLATFORM_ALARM_NOTIFY_SUBSCRIBE = SipConstant.VIDEO_PLATFORM_ALARM_NOTIFY_SUBSCRIBE;
    private DynamicTask dynamicTask;

    public PlatformNotifySubscribeManager(DynamicTask dynamicTask){
        this.dynamicTask= dynamicTask;
    }


    public  NotifySubscribeInfo getCatalogSubscribe(String platformId){
        String key = String.format("%s%s", VIDEO_PLATFORM_ALARM_NOTIFY_SUBSCRIBE, platformId);
        Object o = RedisUtils.get(key);
        return (NotifySubscribeInfo)SerializationUtils.deserialize(Base64.decode((String) o));
    }
    public void removeAlarmSubscribe(String platformId){
        String key = String.format("%s%s", VIDEO_PLATFORM_ALARM_NOTIFY_SUBSCRIBE, platformId);
        //删除订阅信息
        RedisUtils.del(key);
    }

    public void removeAllSubscribe(String platformId) {
        removeAlarmSubscribe(platformId);
    }

}
