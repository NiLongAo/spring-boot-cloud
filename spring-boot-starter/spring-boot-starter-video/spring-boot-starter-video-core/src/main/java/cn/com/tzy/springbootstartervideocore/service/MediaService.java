package cn.com.tzy.springbootstartervideocore.service;

import cn.com.tzy.springbootstartervideocore.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstartervideocore.service.authentication.TokenService;
import cn.hutool.extra.spring.SpringUtil;

public class MediaService {

    public static MediaHookSubscribe getMediaHookSubscribe(){
        return SpringUtil.getBean(MediaHookSubscribe.class);
    }

    public static TokenService getTokenService(){
        return SpringUtil.getBean(TokenService.class);
    }

}
