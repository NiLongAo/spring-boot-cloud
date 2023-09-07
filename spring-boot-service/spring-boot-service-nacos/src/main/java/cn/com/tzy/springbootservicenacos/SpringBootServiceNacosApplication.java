package cn.com.tzy.springbootservicenacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.alibaba.nacos"})
@ServletComponentScan
@EnableScheduling
public class SpringBootServiceNacosApplication {

    /**
     * 是否单机模式启动
     */
    private static String standalone = "true";
    /**
     * 是否开启鉴权
     */
    private static String enabled = "false";

    public static void main(String[] args) {
        System.setProperty("nacos.standalone", standalone);
        System.setProperty("nacos.core.auth.enabled", enabled);
        //System.setProperty("server.port", "8850");
        System.setProperty("server.tomcat.basedir","logs");
        SpringApplication.run(SpringBootServiceNacosApplication.class, args);
    }

}
