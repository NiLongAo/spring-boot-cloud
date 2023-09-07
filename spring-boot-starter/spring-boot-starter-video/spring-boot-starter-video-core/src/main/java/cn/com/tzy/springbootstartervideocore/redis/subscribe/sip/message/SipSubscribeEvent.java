package cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.message;

import cn.com.tzy.springbootstartervideocore.model.EventResult;

@FunctionalInterface
public interface SipSubscribeEvent {
     void response(EventResult eventResult);
}
