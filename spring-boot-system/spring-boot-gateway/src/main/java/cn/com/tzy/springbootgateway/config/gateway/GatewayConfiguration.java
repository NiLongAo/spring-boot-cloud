package cn.com.tzy.springbootgateway.config.gateway;

import cn.com.tzy.springbootgateway.error.MyErrorWebExceptionHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Configuration
public class GatewayConfiguration {

    private final ServerProperties serverProperties;
    private final ApplicationContext applicationContext;
    private final ResourceProperties resourceProperties;
    private final List<ViewResolver> viewResolverList;
    private final ServerCodecConfigurer serverCodecConfigurer;


    public GatewayConfiguration(ServerProperties serverProperties, ApplicationContext applicationContext, ResourceProperties resourceProperties, ObjectProvider<List<ViewResolver>> viewResolverProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolverList = viewResolverProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 自定义网关熔断加载返回错误Json信息
     * @param myblockRequestHandler
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler(BlockRequestHandler myblockRequestHandler){
        //重定向bloack处理
        //GatewayCallbackManager.setBlockHandler(new RedirectBlockRequestHandler("www.baidu.com"));
        //自定义bloack处理
        GatewayCallbackManager.setBlockHandler(myblockRequestHandler);
        return new SentinelGatewayBlockExceptionHandler(viewResolverList,serverCodecConfigurer);
    }
    @Bean(name = "myblockRequestHandler")
    public BlockRequestHandler myblockRequestHandler(){
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                return ServerResponse.status(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("服务器太热，罢工了......"));
            }
        };
        return blockRequestHandler;
    }


    /**
     * 实现 sentinelGateway 网关熔断
     * 根据 配置中网关设置 路由id 设置熔断规则
     * @return
     */
    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter(){
        return new SentinelGatewayFilter();
    }



    /**
     * 自定义网关错误返回json格式数据
     * @param errorAttributes
     * @return
     */
    @Bean("myErrorWebExceptionHandler")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler myErrorWebExceptionHandler(ErrorAttributes errorAttributes){
        MyErrorWebExceptionHandler exceptionHandler = new MyErrorWebExceptionHandler(
                errorAttributes,
                this.resourceProperties,
                this.serverProperties.getError(),
                this.applicationContext
        );
        exceptionHandler.setViewResolvers(this.viewResolverList);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }


}
