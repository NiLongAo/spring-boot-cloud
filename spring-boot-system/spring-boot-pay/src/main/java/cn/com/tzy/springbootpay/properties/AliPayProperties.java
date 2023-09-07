package cn.com.tzy.springbootpay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:bootstrap-${spring.profiles.active}.yml",encoding = "utf-8")
@ConfigurationProperties(prefix = "ali.pay")
public class AliPayProperties {

    private String mchId;
    private String key;
    private String notifyUrl;

}
