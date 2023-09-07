package cn.com.tzy.springbootstarterswagger.config;

import cn.com.tzy.springbootstarterswagger.gateway.handler.SwaggerHandler;
import cn.com.tzy.springbootstarterswagger.gateway.provider.SwaggerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = "swagger.is-gateway",havingValue = "true")
@Import({SwaggerHandler.class, SwaggerProvider.class})
public class SwaggerGatewayConfig {
}
