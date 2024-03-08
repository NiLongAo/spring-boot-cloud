package cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.gateway;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.*;

/**
 * 网关自定义鉴权管理器
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 */
@RequiredArgsConstructor
@Log4j2
public class ResourceServerManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    @Resource
    private TokenStore tokenStore;

    @SneakyThrows
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        // 预检请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        PathMatcher pathMatcher = new AntPathMatcher();
        String method = request.getMethodValue();
        String path = request.getURI().getPath();

        // Restful接口权限设计 @link https://www.cnblogs.com/haoxianrui/p/14961707.html
        //String restfulPath = method + ":" + path;
        String restfulPath =  path;
        Map<String, String> jwtUserMap = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, restfulPath.contains("/socket.io/"), request).builderJwtUser(JwtCommon.AUTHORIZATION_PREFIX, null);
        if(jwtUserMap.isEmpty() && restfulPath.contains("/socket.io/")){
            return Mono.just(new AuthorizationDecision(true));
        }else if(jwtUserMap.isEmpty()){
            return Mono.just(new AuthorizationDecision(false));
        }
        // 缓存取 URL权限-角色集合 规则数据
        // urlPermRolesRules = [{'key':'GET:/api/v1/users/*','value':['ADMIN','TEST']},...]
        Map<String, Object> urlPermRolesRules = (Map<String, Object>) RedisUtils.hmget(Constant.ALL_URL_KEY);
        // 根据请求路径判断有访问权限的角色列表
        List<String> authorizedRoles = new ArrayList<>(); // 拥有访问权限的角色
        boolean requireCheck = false; // 是否需要鉴权，默认“没有设置权限规则”不用鉴权
        for (Map.Entry<String, Object> permRoles : urlPermRolesRules.entrySet()) {
            String perm = permRoles.getKey();
            if (pathMatcher.match(perm, restfulPath)) {
                List<String> roles = Convert.toList(String.class, permRoles.getValue());
                authorizedRoles.addAll(Convert.toList(String.class, roles));
                requireCheck = true;
                break;
            }
        }
        if (!requireCheck) {
            return Mono.just(new AuthorizationDecision(true));
        }
        OAuth2Authentication authenticationFromCache = (OAuth2Authentication) tokenStore.readAuthentication(MapUtil.getStr(jwtUserMap, JwtCommon.JWT_AUTHORIZATION_KEY));
        // 判断JWT中携带的用户角色是否有权限访问
        return mono.flatMapIterable(authentication -> {
            Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
            if (authenticationFromCache != null) {
                authorities = authenticationFromCache.getAuthorities();
            }
            return authorities;
        })
        .map(GrantedAuthority::getAuthority)
        .any(authority -> {
            //String roleCode = authority.substring(Common.AUTHORITY_PREFIX.length()); // 用户的角色
            return CollectionUtil.isNotEmpty(authorizedRoles) && authorizedRoles.contains(authority);
        })
        .map(AuthorizationDecision::new)
        .defaultIfEmpty(new AuthorizationDecision(false));

    }
}
