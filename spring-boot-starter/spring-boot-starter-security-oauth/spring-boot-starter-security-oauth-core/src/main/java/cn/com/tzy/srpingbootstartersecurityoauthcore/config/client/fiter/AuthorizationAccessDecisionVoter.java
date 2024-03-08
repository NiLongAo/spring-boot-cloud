package cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.fiter;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j2
public class AuthorizationAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {
    private final TokenStore tokenStore;
    public AuthorizationAccessDecisionVoter(TokenStore tokenStore) {
        this.tokenStore =tokenStore;
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> collection) {
        assert authentication != null;
        assert object != null;
        // 拿到当前请求uri
        
        String restfulPath = object.getRequestUrl();
        String method = object.getRequest().getMethod();
        Map<String, String> jwtUserMap = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, false, object.getRequest()).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        if(jwtUserMap.isEmpty()){
            return ACCESS_DENIED;
        }
        log.debug("进入自定义鉴权投票器，URI : {} {}", method, restfulPath);
        PathMatcher pathMatcher = new AntPathMatcher();
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
            return ACCESS_GRANTED;
        }
        OAuth2Authentication authenticationFromCache = (OAuth2Authentication) tokenStore.readAuthentication(MapUtil.getStr(jwtUserMap, JwtCommon.JWT_AUTHORIZATION_KEY));
        if(authenticationFromCache == null){
            return ACCESS_DENIED;
        }
        return ACCESS_GRANTED;
    }
//


    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }


}
