package cn.com.tzy.springbootstarterfreeswitch.config.fs;


import cn.com.tzy.springbootcomm.utils.DynamicTask;
import link.thingscloud.freeswitch.esl.spring.boot.starter.EnableFreeswitchEslAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Import({DynamicTask.class})
@ComponentScan("cn.com.tzy.springbootstarterfreeswitch")
@EnableFreeswitchEslAutoConfiguration
public class FreeswitchConfig {

}
