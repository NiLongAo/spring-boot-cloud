package cn.com.tzy.springbootstartersentinel.config;

import cn.com.tzy.springbootstartersentinel.config.sentinel.SentinelExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix ="spring.cloud.sentinel.gateway",value = "enable",havingValue = "false", matchIfMissing= true)
public class SentinelConfig {

    /**
     * 非gateway网关熔断限流自定义异常
     * @return
     */
    @Bean
    public SentinelExceptionHandler blockExceptionHandler(){
       return new SentinelExceptionHandler();
    }


}
