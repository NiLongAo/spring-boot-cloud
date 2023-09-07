package cn.com.tzy.springbootpay.properties;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:bootstrap-${spring.profiles.active}.yml",encoding = "utf-8")
@ConfigurationProperties(prefix = "cp.ali.pay")
public class CpAliPayProperties {

    private String protocol;
    private String gatewayHost;
    private String signType;
    private String appId;
    private String merchantPrivateKey;
    private String aliPayPublicKey;
    private String notifyUrl;
    private String encryptKey;

    @Bean
    public Config ConfigAliPay() {
        Config config = new Config();
        config.protocol = protocol;
        config.gatewayHost = gatewayHost;
        config.signType = signType;
        config.appId = appId;
        /**
         * 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
         */
        config.merchantPrivateKey = merchantPrivateKey;
        config.alipayPublicKey = aliPayPublicKey;
        config.notifyUrl = notifyUrl;
        config.encryptKey = encryptKey;
        Factory.setOptions(config);
        return config;
    }
}