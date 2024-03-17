package cn.com.tzy.springbootcomm.utils;


import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.hutool.core.map.MapUtil;
import com.nimbusds.jose.JWSObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author xianrui
 */
@Log4j2
public class JwtUtils {
    /**
     * 解析JWT获取用户ID
     * @return
     */
    public static Map<String, String> getJwtUserMap(){
        return   builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .builderJwtUser( null);
    }
    public static Long getUserId(){
        String userId = builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .buildNameValue(JwtCommon.JWT_USER_ID, false);
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        return Long.parseLong(userId);
    }

    /**
     * 获取登录账户
     */
    public static String getUserName(){
        return builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .buildNameValue( JwtCommon.JWT_USER_NAME,false);
    }
    /**
     * 获取HttpServletRequest 中的  schemasTenantId
     * @return
     */
    public static Long getSchemasTenantId(){
        String schemasTenantId = builder().buildNameValue(Constant.SCHEMAS_TENANT_ID,true);
        if(StringUtils.isEmpty(schemasTenantId)){
            return null;
        }
        return Long.parseLong(schemasTenantId);
    }
    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static Long getTenantId() {
        String tenantId = builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .buildNameValue(JwtCommon.JWT_TENANT_ID, false);
        if(StringUtils.isEmpty(tenantId)){
            return null;
        }
        return Long.parseLong(tenantId);
    }

    public static String getAuthorization(boolean isPrefix) {
        if(isPrefix){
            // 从请求路径中获取
            return builder().buildNameValue(JwtCommon.JWT_AUTHORIZATION_KEY,true);
        }else {
            return builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                    .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                    .buildNameValue(JwtCommon.JWT_AUTHORIZATION_KEY,false);
        }
    }
    /**
     * 解析JWT获取用户是否超级管理员
     *
     * @return
     */
    public static boolean getAdministrator() {
        Map<String, String> map = builder(JwtCommon.JWT_AUTHORIZATION_KEY, false)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .builderJwtUser(null);
        if(!MapUtil.getBool(map,JwtCommon.JWT_ADMIN,false)){
            return false;
        }
        return Constant.TENANT_ID.equals(MapUtil.getLong(map, JwtCommon.JWT_TENANT_ID, null));
    }

    /**
     * 获取登录认证的客户端ID
     * 兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     *
     * @return
     */
    public static String getOAuthClientId() {
        String clientId = builder().buildNameValue(JwtCommon.JWT_CLIENT_ID,false);
        if (StringUtils.isBlank(clientId)) {
            clientId = builder(JwtCommon.JWT_AUTHORIZATION_KEY)
                    .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                    .buildNameValue( JwtCommon.JWT_CLIENT_ID,false);
        }
        return clientId;
    }


    public static String getLoginType() {
        // 从请求路径中获取
        String loginType = builder().buildNameValue(JwtCommon.JWT_LOGIN_TYPE,false);
        if(StringUtils.isEmpty(loginType)){
            loginType = builder(JwtCommon.JWT_AUTHORIZATION_KEY)
                    .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                    .buildNameValue(JwtCommon.JWT_LOGIN_TYPE,false);
        }
        // 从请求路径中获取
        return loginType;
    }



    public static Builder builder(){
        return builder(null,false);
    }
    public static Builder builder(String jwtAuthorizationKey){
       return builder(jwtAuthorizationKey,false);
    }

    public static Builder builder(String jwtAuthorizationKey,String authorization){
        return new Builder(jwtAuthorizationKey,authorization,false,(HttpServletRequest)null);
    }
    public static Builder builder(String jwtAuthorizationKey,boolean isParameter) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if(requestAttributes != null){
            request = requestAttributes.getRequest();
        }
        return new Builder( jwtAuthorizationKey,null,isParameter,request);
    }
    public static Builder builder(String jwtAuthorizationKey,HttpServletRequest request) {
        return new Builder( jwtAuthorizationKey,null,false,request);
    }
    public static Builder builder(String jwtAuthorizationKey,ServerHttpRequest request) {
        return new Builder( jwtAuthorizationKey,null,false,request);
    }
    public static Builder builder(String jwtAuthorizationKey,boolean isParameter,HttpServletRequest request) {
        return new Builder( jwtAuthorizationKey,null,isParameter,request);
    }
    public static Builder builder(String jwtAuthorizationKey,boolean isParameter,ServerHttpRequest request) {
        return new Builder( jwtAuthorizationKey,null,isParameter,request);
    }
    public static class Builder{
        private final String jwtAuthorizationKey;
        private  String authorization;
        private  String prefix;
        private final boolean isParameter;
        private final HttpServletRequest requestHttp;
        private final ServerHttpRequest requestServer;


        public Builder(String jwtAuthorizationKey,String authorization,boolean isParameter,HttpServletRequest requestHttp) {
            this.jwtAuthorizationKey = jwtAuthorizationKey;
            this.authorization = authorization;
            this.isParameter = isParameter;
            this.requestHttp = requestHttp;
            this.requestServer = null;
        }

        public Builder(String jwtAuthorizationKey,String authorization,boolean isParameter,ServerHttpRequest requestServer) {
            this.jwtAuthorizationKey = jwtAuthorizationKey;
            this.authorization = authorization;
            this.isParameter = isParameter;
            this.requestHttp = null;
            this.requestServer = requestServer;
        }

        public Builder setPrefix(String prefix){
            this.prefix = prefix;
            return this;
        }


        public Map<String, String> builderJwtUser(String payloadName){
            if(StringUtils.isEmpty(authorization) && (requestHttp != null || requestServer != null)){
                findAuthorization();
            }
            if(StringUtils.isEmpty(authorization)){
                log.warn("未从 HttpServletRequest 没有认证参数 Authorization");
                return new HashMap<>();
            }
            return buildJWSObject(payloadName);
        }
        public String buildNameValue(String name,boolean isHeader){
            String val = null;
            findAuthorization();
            if(authorization != null){
                Map<String, String> authorization = buildJWSObject(null);
                val = MapUtil.getStr(authorization, name);
            }else if(requestHttp != null){
                if(isHeader){
                    val = requestHttp.getHeader(name);
                }else {
                    val =   requestHttp.getParameter(name);
                }
            }else if(requestServer != null){
                if(isHeader){
                    val = requestServer.getHeaders().getFirst(name);
                }else {
                    val =   requestServer.getQueryParams().getFirst(name);
                }
            }
            return val;
        }
        private Map<String, String> buildJWSObject(String payloadName){
            if(StringUtils.isNotEmpty(prefix)){
                if(!prefix.startsWith(JwtCommon.AUTHORIZATION_PREFIX)){
                    log.error("token缺少前缀：{}",JwtCommon.AUTHORIZATION_PREFIX);
                    return new HashMap<>();
                }
                authorization = authorization.replace(prefix, Strings.EMPTY);
            }
            JWSObject jwsObject = null;
            try {
                jwsObject =JWSObject.parse(authorization);
            } catch (ParseException e) {
                log.error("解析JWT失败",e);
                return new HashMap<>();
            }
            String payload = jwsObject.getPayload().toString();
            if(StringUtils.isBlank(payload)){
                log.error("请求头解析身份,为空");
                return new HashMap<>();
            }
            Map<String, String> map;
            try {
                map = (Map<String,String>) AppUtils.decodeJson2(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()), Map.class);
            } catch (Exception e) {
                log.error("请求头解析失败",e);
                return new HashMap<>();
            }
            map.put(jwtAuthorizationKey, authorization);
            if(StringUtils.isNotEmpty(payloadName)){
                map.put(payloadName, payload);
            }
            return map;
        }
        private void findAuthorization(){
            if(StringUtils.isNotEmpty(jwtAuthorizationKey)){
                if(requestHttp != null){
                    authorization = requestHttp.getHeader(jwtAuthorizationKey);
                    if(StringUtils.isEmpty(authorization) && isParameter){
                        authorization =   requestHttp.getParameter(jwtAuthorizationKey);
                    }
                }else if(requestServer != null){
                    authorization = requestServer.getHeaders().getFirst(jwtAuthorizationKey);
                    if(StringUtils.isEmpty(authorization) && isParameter){
                        authorization =   requestServer.getQueryParams().getFirst(jwtAuthorizationKey);
                    }
                }
            }
        }
    }
}
