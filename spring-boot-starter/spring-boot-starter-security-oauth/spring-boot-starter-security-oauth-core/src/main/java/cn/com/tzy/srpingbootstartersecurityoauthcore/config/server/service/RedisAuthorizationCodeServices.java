package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server.service;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Component;

public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    String AUTH_CODE_PREFIX = "security:code:auth:";
    /**
     * 将存储code到redis，并设置过期时间，10分钟
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        RedisUtils.set(redisKey(code), authentication, 10 * 60);
    }

    @Override
    protected OAuth2Authentication remove(final String code) {
        String codeKey = redisKey(code);
        OAuth2Authentication token = null;
        if(RedisUtils.hasKey(codeKey)){
            token = (OAuth2Authentication) RedisUtils.get(codeKey);
            RedisUtils.del(codeKey);
        }
        return token;
    }

    /**
     * redis中 code key的前缀
     */
    private String redisKey(String code) {
        return AUTH_CODE_PREFIX + code;
    }
}

