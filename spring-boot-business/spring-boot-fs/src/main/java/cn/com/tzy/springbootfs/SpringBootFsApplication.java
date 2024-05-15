package cn.com.tzy.springbootfs;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient //开启nacos服务站注册发现
@EnableFeignClients(basePackages = {"cn.com.tzy.springbootfeignsso.api","cn.com.tzy.springbootfeignbean.api"}) //开启feign
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class})
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootFsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootFsApplication.class, args);
    }
}