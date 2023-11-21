package cn.com.tzy.springbootbean;

import cn.com.tzy.springbootstarternacos.config.rule.MyRibbonConfig;
import cn.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

@EnableDiscoveryClient //开启nacos服务站注册发现
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@RibbonClients(defaultConfiguration = MyRibbonConfig.class)
@EsMapperScan("cn.com.tzy.springbootbean.mapper.es")
@EnableCaching //开启caching
public class SpringBootBeanApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootBeanApplication.class, args);
    }

}
