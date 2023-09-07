package cn.com.tzy.srpingbootstartersecurityoauthcore.config.server;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.com.tzy.srpingbootstartersecurityoauthcore.properties.WxMaProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信相关业务注册
 */
@Configuration
@EnableConfigurationProperties(WxMaProperties.class)
@ConditionalOnProperty(value = "security-oauth.type",havingValue = "sso")
public class WxConfig {

    /**
     * 微信小程序相关注册
     * @param properties
     * @return
     */
    @Bean
    public WxMaService wxMaService(WxMaProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        config.setMsgDataFormat(properties.getMsgDataFormat());
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
