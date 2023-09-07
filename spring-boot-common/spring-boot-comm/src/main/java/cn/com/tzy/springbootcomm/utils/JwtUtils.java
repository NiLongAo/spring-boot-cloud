package cn.com.tzy.springbootcomm.utils;


import cn.com.tzy.springbootcomm.constant.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author xianrui
 */
@Log4j2
public class JwtUtils {

    public static Map getJwtPayload() {
        String payload = getPayload();
        Map map = null;
        try {
            if(StringUtils.isBlank(payload)){
                map = new HashMap();
                log.info("请求头解析身份,为空");
            }else{
                map = (Map) AppUtils.decodeJson2(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()), Map.class);
            }
        } catch (Exception e) {
            log.error("请求头解析失败[]",e);
        }
        return map;
    }

    public static String getPayload(){
        if(getHttpServlet() == null){
            return null;
        }
        return getHttpServlet().getHeader(Constant.JWT_PAYLOAD_KEY);
    }

    public static String getAuthorization(){
        if(getHttpServlet() == null){
            return null;
        }
        return getHttpServlet().getHeader(Constant.AUTHORIZATION_KEY);
    }

    public static Long getSchemasTenantId(){
        if(getHttpServlet() == null){
            return null;
        }
        String tenantId = getHttpServlet().getHeader(Constant.SCHEMAS_TENANT_ID);
        if(StringUtils.isBlank(tenantId)){
            return null;
        }
        return Long.parseLong(tenantId);
    }



    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static Long getUserId() {
        Object object = getJwtPayload().get(Constant.USER_ID_KEY);
        if(object == null){
            return null;
        }
        return Long.valueOf(object.toString());
    }
    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static Long getTenantId() {
        Object object = getJwtPayload().get(Constant.TENANT_ID_KEY);
        if(object == null){
            return null;
        }
        return Long.parseLong(String.valueOf(getJwtPayload().get(Constant.TENANT_ID_KEY)));
    }


    /**
     * 获取登录认证的客户端ID
     *
     * 兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     *
     * @return
     */
    public static String getOAuthClientId() {
        String clientId;
        if(getHttpServlet() == null){
            return null;
        }
        // 从请求路径中获取
        clientId = getHttpServlet().getParameter(Constant.CLIENT_ID_KEY);
        if (StringUtils.isNotBlank(clientId)) {
            return clientId;
        }
        // 从请求头获取
        Object object = getJwtPayload().get(Constant.CLIENT_ID_KEY);
        if(object != null){
            clientId = String.valueOf(object);
        }
        return clientId;
    }


    public static String getLoginType() {
        if(getHttpServlet() == null){
            return null;
        }
        // 从请求路径中获取
        return getHttpServlet().getParameter(Constant.LOGIN_TYPE);
    }

    public static HttpServletRequest getHttpServlet(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null){
            return null;
        }
        return requestAttributes.getRequest();
    }
}
