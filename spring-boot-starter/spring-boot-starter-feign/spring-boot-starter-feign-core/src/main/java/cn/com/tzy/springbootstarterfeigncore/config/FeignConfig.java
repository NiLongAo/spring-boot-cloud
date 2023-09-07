package cn.com.tzy.springbootstarterfeigncore.config;

import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({FeignConfiguration.class})
public class FeignConfig {
}
