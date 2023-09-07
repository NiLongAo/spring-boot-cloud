package cn.com.tzy.springbootstarterminio.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haopeng
 */
@Data
@SuperBuilder(toBuilder = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * minio地址
     */
    private String endpoint;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 账号
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;


}
