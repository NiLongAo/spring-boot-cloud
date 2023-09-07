package cn.com.tzy.springbootstarterfeignconfig.feign;

import cn.com.tzy.springbootstarterfeignconfig.aspect.FeignExceptionAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudServerConfiguration {

  @Bean
  @ConditionalOnProperty( name = "system.cloud.feign.exception.enabled", havingValue = "true", matchIfMissing = true)
  public FeignExceptionAspect feignExceptionAspect() {
    // 微服务权限拦截处理
    return new FeignExceptionAspect();
  }
}
