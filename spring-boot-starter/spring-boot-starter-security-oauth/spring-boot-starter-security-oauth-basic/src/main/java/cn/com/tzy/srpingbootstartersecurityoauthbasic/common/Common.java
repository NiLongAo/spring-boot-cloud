package cn.com.tzy.srpingbootstartersecurityoauthbasic.common;

public class Common {

    /**
     * JWT 标识当前用户是否是系统管理员
     */
    public final static String JWT_ADMIN = "isAdmin";
    /**
     * JWT 当前用户编号
     */
    public final static String JWT_USER_ID = "userId";
    /**
     * JWT 当前用户编号所属租户
     */
    public final static String JWT_TENANT_ID = "tenantId";
    /**
     * JWT ID 唯一标识
     */
    public final static String JWT_JTI = "jti";

    /**
     * JWT令牌前缀
     */
    public final static  String AUTHORIZATION_PREFIX = "Bearer ";

    /**
     * JWT存储权限前缀
     */
    public final static String AUTHORITY_PREFIX = "PRIVILEGE_";
    /**
     * JWT存储权限属性
     */
    public final static  String JWT_AUTHORITIES_KEY = "authorities";


    /**
     * 验证码登录请求参数：手机号码
     */
    public static final String SPRING_SECURITY_RESTFUL_CLIENT_ID_KEY = "client_id";
    /**
     * 验证码登录请求参数：短信验证码
     */
    public static final String SPRING_SECURITY_RESTFUL_CLIENT_SECRET_KEY = "client_secret";
}
