package cn.com.tzy.springbootpay;

import cn.com.tzy.springbootpay.config.StartupRunner;
import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient //开启nacos服务站注册发现
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
public class SpringBootPayApplication {

  @Bean
  public StartupRunner startupRunner() {
    return new StartupRunner();
  }
  public static void main(String[] args) {
    SpringApplication.run(SpringBootPayApplication.class, args);
  }
}
