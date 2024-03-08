package cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.gateway;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.srpingbootstartersecurityoauthcore.utils.ResponseUtils;
import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

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
        Map<String, String> jwtUserMap = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, request.getURI().getPath().contains("/socket.io/"), request).builderJwtUser(JwtCommon.AUTHORIZATION_PREFIX, null);
        if(jwtUserMap.isEmpty() ||  request.getURI().getPath().contains("/socket.io/")){
            return chain.filter(exchange);
        }
        //验证token是否有效
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(MapUtil.getStr(jwtUserMap, JwtCommon.JWT_AUTHORIZATION_KEY));
        if(oAuth2AccessToken == null){
            return ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
        }else if(oAuth2AccessToken.isExpired()){
            return ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
        }
        boolean ok = false;
        if(!oAuth2AccessToken.getScope().contains("all")){
            for (String scope : oAuth2AccessToken.getScope()) {
                if(request.getURI().getPath().startsWith(scope)){
                    ok = true;
                    break;
                }
            }
        }else {
            ok =true;
        }
        if(!ok){
            return ResponseUtils.writeErrorInfo(response, RespCode.CODE_316);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
