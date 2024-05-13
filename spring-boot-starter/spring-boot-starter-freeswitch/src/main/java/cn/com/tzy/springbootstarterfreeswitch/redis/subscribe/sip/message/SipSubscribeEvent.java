package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message;


import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;

@FunctionalInterface
public interface SipSubscribeEvent {
     void response(EventResult eventResult);
}
