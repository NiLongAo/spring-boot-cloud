package cn.com.tzy.springbootstarterswagger.config;

import cn.com.tzy.springbootstarterswagger.properties.SwaggerProperties;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI  //第三方swagger增强API注解
@EnableConfigurationProperties(SwaggerProperties.class)
@RequiredArgsConstructor
@ConditionalOnProperty(value = "swagger.enable",havingValue = "true")
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    @ConditionalOnWebApplication
    public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .enable(swaggerProperties.isEnable())
        .groupName(swaggerProperties.getGroupName())
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApis()))
        .paths(PathSelectors.any())
        .build();
    }

    private ApiInfo apiInfo() {
        SwaggerProperties.ApiInfo apiInfo = swaggerProperties.getApiInfo();
        return new ApiInfoBuilder()
                .title(apiInfo.getTitle())
                .description(apiInfo.getDescription())
                .termsOfServiceUrl(apiInfo.getTermsOfServiceUrl())
                .version(apiInfo.getVersion())
                .build();
    }
}
