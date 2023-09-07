package cn.com.tzy.springbootapp;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "cn.com.tzy.springbootfeignbean.api",
        "cn.com.tzy.springbootfeignsms.api",
        "cn.com.tzy.springbootfeignsso.api",
        "cn.com.tzy.springbootfeignoa.api",
        "cn.com.tzy.springbootfeignacitiviti.api",
        "cn.com.tzy.springbootfeignface.api",
}) //开启feign
@EnableDiscoveryClient //开启nacos服务站注册发现
@SpringBootApplication
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAppApplication.class, args);
    }

}
