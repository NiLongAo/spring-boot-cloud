package cn.com.tzy.springbootstartervideocore.redis.subscribe.media;

import cn.com.tzy.springbootstartervideobasic.vo.media.HookVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;

@FunctionalInterface
public interface HookEvent {
    void response(MediaServerVo mediaServerVo, HookVo response);
}
