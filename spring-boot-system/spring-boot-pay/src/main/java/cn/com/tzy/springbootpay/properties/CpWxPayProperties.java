package cn.com.tzy.springbootpay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 企业微信
 */
@Data
@Configuration
@PropertySource(value = "classpath:bootstrap-${spring.profiles.active}.yml",encoding = "utf-8")
@ConfigurationProperties(prefix = "cp.wx.pay")
public class CpWxPayProperties {

    private String appId;
    private String appSecret;
    private String mchId;
    private String apiKey;
    private String signType;
    private String certPath;
    private String notifyUrl;

}
