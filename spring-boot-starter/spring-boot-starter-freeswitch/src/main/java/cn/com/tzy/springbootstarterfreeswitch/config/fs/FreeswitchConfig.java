package cn.com.tzy.springbootstarterfreeswitch.config.fs;


import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.strategy.CallStrategyHandler;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.spring.boot.starter.EnableFreeswitchEslAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Import({DynamicTask.class})
@ComponentScan("cn.com.tzy.springbootstarterfreeswitch")
@EnableFreeswitchEslAutoConfiguration
public class FreeswitchConfig {

    //注册IVR处理类
    @Resource
    private InboundClient inboundClient;
    @Resource
    private DynamicTask dynamicTask;
    //流程注册
    @Bean
    public ProcessNextHandler processNextHandler(){
        return new ProcessNextHandler(new CallStrategyHandler(inboundClient));
    }

}
