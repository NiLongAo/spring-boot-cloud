package cn.com.tzy.springbootstarterxxljob.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haopeng
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    private String accessToken;

    private Admin admin;

    private Executor executor;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Admin{
        private String addresses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Executor{
        private String appname;
        private String address;
        private String ip;
        private int port;
        private String logPath;
        private int logRetentionDays;
    }

}
