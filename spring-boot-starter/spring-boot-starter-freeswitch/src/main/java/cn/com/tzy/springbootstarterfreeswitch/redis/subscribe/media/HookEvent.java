package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media;


import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;

@FunctionalInterface
public interface HookEvent {
    void response(MediaServerVo mediaServerVo, HookVo response);
}
