package cn.com.tzy.springbootstarterfeign.config;

import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({FeignConfiguration.class})
public class FeignConfig {
}
