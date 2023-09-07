package cn.com.tzy.springbootstarterswagger.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 是否开启
     */
    private boolean enable = false ;

    /**
     * 是否开启
     */
    private boolean isGateway = false ;

    /**
     * 分组名称
     */
    private String groupName = "" ;

    /**
     * 接口所在路径
     */
    private String apis= "" ;

    /**
     * 接口所在路径
     */
    private ApiInfo apiInfo = new ApiInfo();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiInfo{
        /**
         * 标题
         */
        private String title= "" ;
        /**
         *  描述
         */
        private String description= "" ;
        /**
         * 介绍地址
         */
        private String termsOfServiceUrl= "" ;
        /**
         * 版本
         */
        private String version= "" ;
    }


}
