package cn.com.tzy.springbootstarterminio.config;

import cn.com.tzy.springbootstarterminio.properties.MinioProperties;
import cn.com.tzy.springbootstarterminio.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioProperties.class)
@Import({MinioUtils.class})
public class MinioConfig {

}
