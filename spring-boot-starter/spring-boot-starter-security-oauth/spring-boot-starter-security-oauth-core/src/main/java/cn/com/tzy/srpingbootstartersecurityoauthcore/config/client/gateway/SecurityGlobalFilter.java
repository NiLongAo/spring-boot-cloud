package cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.gateway;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.Common;
import cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.utils.ResponseUtils;
import com.nimbusds.jose.JWSObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;

/**
 * 安全拦截全局过滤器
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 */
@Log4j2
@RequiredArgsConstructor
public class SecurityGlobalFilter implements GlobalFilter, Ordered {

    private final TokenStore tokenStore;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 非JWT或者JWT为空不作处理
        String token = request.getHeaders().getFirst(Constant.AUTHORIZATION_KEY);
        if(StringUtils.isEmpty(token) && request.getURI().getPath().contains("/socket.io/")){
            token = request.getQueryParams().getFirst(Constant.AUTHORIZATION_KEY);
        }
        if (StringUtils.isEmpty(token) || !token.startsWith(Common.AUTHORIZATION_PREFIX)) {
            return chain.filter(exchange);
        }
        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在拦截响应token失效
        token = token.replace(Common.AUTHORIZATION_PREFIX, Strings.EMPTY);
        //验证token是否有效
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if(oAuth2AccessToken == null){
            return ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
        }else if(oAuth2AccessToken.isExpired()){
            return ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
        }
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        // 存在token且不是黑名单，request写入JWT的载体信息
        request = exchange.getRequest().mutate()
                .header(Constant.JWT_PAYLOAD_KEY, URLEncoder.encode(payload,"UTF-8"))
                .header(Constant.AUTHORIZATION_KEY, URLEncoder.encode(token,"UTF-8"))
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
