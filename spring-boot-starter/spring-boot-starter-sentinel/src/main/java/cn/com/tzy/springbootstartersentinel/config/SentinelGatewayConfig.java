package cn.com.tzy.springbootstartersentinel.config;

import cn.com.tzy.springbootstartersentinel.config.sentinel.SentinelGatewayExceptionHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix ="spring.cloud.sentinel.gateway",value = "enable",havingValue = "true")
public class SentinelGatewayConfig {

    /**
     *
     * gateway网关熔断限流自定义异常
     * @return
     */
    @Bean
    public SentinelGatewayExceptionHandler sentinelGatewayExceptionHandler(){
        SentinelGatewayExceptionHandler sentinelGatewayExceptionHandler = new SentinelGatewayExceptionHandler();
        GatewayCallbackManager.setBlockHandler(sentinelGatewayExceptionHandler);
        return sentinelGatewayExceptionHandler;
    }

}
