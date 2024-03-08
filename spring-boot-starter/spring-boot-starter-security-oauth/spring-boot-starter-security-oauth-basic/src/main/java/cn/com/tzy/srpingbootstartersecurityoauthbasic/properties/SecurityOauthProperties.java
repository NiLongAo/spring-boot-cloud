package cn.com.tzy.srpingbootstartersecurityoauthbasic.properties;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "security-oauth")
public class SecurityOauthProperties {

    private String type;

    private List<String> ignoreUrls;
}
