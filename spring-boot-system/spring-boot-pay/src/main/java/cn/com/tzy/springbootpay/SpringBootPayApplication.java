package cn.com.tzy.springbootpay;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@SpringBootApplication
@EnableDiscoveryClient //开启nacos服务站注册发现
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootPayApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBootPayApplication.class, args);
  }
}
