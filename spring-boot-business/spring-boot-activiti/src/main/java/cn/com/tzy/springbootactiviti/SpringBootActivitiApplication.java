package cn.com.tzy.springbootactiviti;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"cn.com.tzy.springbootfeignbean.api","cn.com.tzy.springbootfeignoa.api"}) //开启feign
@EnableDiscoveryClient //开启nacos服务站注册发现
@SpringBootApplication(
        exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class,
                DataSourceAutoConfiguration.class
        })
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootActivitiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootActivitiApplication.class, args);
    }

}
