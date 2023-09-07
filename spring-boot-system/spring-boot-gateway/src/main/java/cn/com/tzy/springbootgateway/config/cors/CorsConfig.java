package cn.com.tzy.springbootgateway.config.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@ConditionalOnBean(GlobalCorsProperties.class)
public class CorsConfig {

    private final Environment environment;

    /**
     * 解决跨域问题
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        //开发环境允许跨域
        if(environment.acceptsProfiles(Profiles.of("dev"))){
            configuration.addAllowedOrigin("*");//请求域名
        }else {
            configuration.addAllowedOrigin("http://1.15.9.228:9200");//请求域名
        }
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));//请求方法
        configuration.setAllowedHeaders(Collections.singletonList("*"));//请求头
        //添加响应头
        configuration.addAllowedHeader("Authenticate");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(source);
    }

}
