package cn.com.tzy.srpingbootstartersecurityoauthcore.config.client.fiter;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.properties.SecurityOauthProperties;
import cn.com.tzy.srpingbootstartersecurityoauthcore.utils.ResponseUtils;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * token的校验
 * 该类继承自BasicAuthenticationFilter，在doFilterInternal方法中，
 * 从http头的Authorization 项读取token数据，然后用Jwts包提供的方法校验token的合法性。
 * 如果校验通过，就认为这是一个取得授权的合法请求
 * @author xxm
 */
@Log4j2
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private TokenStore tokenStore;
    private String serverName;

    public JWTAuthenticationFilter(TokenStore tokenStore, AuthenticationManager authenticationManager,String serverName) {
        super(authenticationManager);
        this.tokenStore =tokenStore;
        this.serverName =serverName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String restfulPath = request.getRequestURI();
        if(StringUtils.isNotEmpty(serverName) && restfulPath.startsWith(serverName)){
            restfulPath = restfulPath.substring(serverName.length(),restfulPath.length());
        }
        boolean ok =false;
        PathMatcher pathMatcher = new AntPathMatcher();
        SecurityOauthProperties bean = SpringUtil.getBean(SecurityOauthProperties.class);
        for (String ignoreUrl : bean.getIgnoreUrls()) {
            if(pathMatcher.match(ignoreUrl,restfulPath)){
                ok = true;
                break;
            }
        }
        if(ok){
            chain.doFilter(request, response);
            return;
        }
        Map<String, String> jwtUserMap = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, restfulPath.contains("/socket.io/"), request).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        if (jwtUserMap.isEmpty()) {
            ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
            return ;
        }
        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在拦截响应token失效
        //验证token是否有效
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(MapUtil.getStr(jwtUserMap, JwtCommon.JWT_AUTHORIZATION_KEY));
        if(oAuth2AccessToken == null){
            ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
            return ;
        }else if(oAuth2AccessToken.isExpired()){
            ResponseUtils.writeErrorInfo(response, RespCode.CODE_314);
            return ;
        }
        ok =false;
        if(!oAuth2AccessToken.getScope().contains("all")){
            for (String scope : oAuth2AccessToken.getScope()) {
                if(restfulPath.startsWith(scope)){
                    ok = true;
                    break;
                }
            }
        }else {
            ok = true;
        }
        if(!ok){
            ResponseUtils.writeErrorInfo(response, RespCode.CODE_316);
            return ;
        }
        chain.doFilter(request, response);
    }

}
