package cn.com.tzy.springbootservicesentinel;

import com.alibaba.csp.sentinel.init.InitExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sentinel dashboard application.
 *
 * @author Carpenter Lee
 */
@SpringBootApplication(scanBasePackages = "com.alibaba.csp.sentinel.dashboard")
public class SpringBootServiceSentinelApplication {
    public static void main(String[] args) {
        triggerSentinelInit();
        SpringApplication.run(SpringBootServiceSentinelApplication.class, args);
    }
    private static void triggerSentinelInit() {
        new Thread(() -> InitExecutor.doInit()).start();
    }
}
