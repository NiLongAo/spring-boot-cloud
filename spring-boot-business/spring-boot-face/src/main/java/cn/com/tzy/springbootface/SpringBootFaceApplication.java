package cn.com.tzy.springbootface;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@EnableDiscoveryClient //开启nacos服务站注册发现
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootFaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFaceApplication.class, args);
    }

}
