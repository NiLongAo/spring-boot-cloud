package cn.com.tzy.springbootserviceseata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"io.seata"})
public class SpringBootServiceSeataApplication {

    public static void main(String[] args) throws IOException {
        // run the spring-boot application
        SpringApplication.run(SpringBootServiceSeataApplication.class, args);
    }
}
