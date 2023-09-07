package cn.com.tzy.springbootstartercloud.config;

import cn.com.tzy.springbootstartercloud.web.aspect.LoggerAspect;
import cn.com.tzy.springbootstartercloud.web.interceptor.LogonInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LoggerAspect.class, LogonInterceptor.class})
public class ClientConfig {
}
