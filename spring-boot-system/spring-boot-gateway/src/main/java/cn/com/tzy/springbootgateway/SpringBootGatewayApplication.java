package cn.com.tzy.springbootgateway;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableDiscoveryClient //开启nacos服务站注册发现
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootGatewayApplication.class, args);
    }
}
