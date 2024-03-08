package cn.com.tzy.springbootcomm.common.jwt;

public class JwtCommon {

    /**
     * JWT 标识当前用户是否是系统管理员
     */
    public final static String JWT_ADMIN = "is_admin";
    /**
     * JWT 当前用户编号
     */
    public final static  String JWT_USER_ID = "user_id";
    /**
     */
    public final static String JWT_CLIENT_ID = "client_id";
    /**
     * JWT 当前用户名
     */
    public final static  String JWT_USER_NAME = "user_name";
    /**
     * JWT 当前用户编号所属租户
     */
    public final static String JWT_TENANT_ID = "tenant_id";
    /**
     * JWT 当前用户编号所属租户
     */
    public final static String JWT_LOGIN_TYPE = "login_type";

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

    /*******************************************************/
    /**
     * 认证请求头key
     */
    public final static  String JWT_AUTHORIZATION_KEY = "Authorization";
}
