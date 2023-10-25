package cn.com.tzy.springbootcomm.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * 常量池
 * @author TZY
 */
public class Constant {

    /**
     * 签名加密
     */
    public static final String SECRET_KEY = "Lp0jtuCAwVfz@N8m";
    public static final String SECRET_IV = "z3UBSAGj#$6hJ9LV";

    public final static Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
    /**
     * 下划线匹配
     */
    public static Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
    /**
     * 大写匹配
     */
    public static Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    /**
     * 转换时间格式
     */
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATETIME_FORMAT = "yyyyMMddHHmmss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String HOUR_MINUTE = "HH:mm";
    public final static String MONTH_FORMAT = "yyyy-MM";
    public final static String YEAR_FORMAT = "yyyy";
    /**
     * 图片格式
     */
    public final static String[] IMAGE_SUFFIX = {"png", "jpeg", "bmp", "jpg", "gif", "ico"};
    /**
     * 文件暂存地址
     */
    public final static String PATH_TEMP = "/static/temp/";
    /**
     * 默认公共租户编号
     */
    public final static Long TENANT_ID = 1L;

    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public final static int EXRP_MINUTE = 60;

    /**
     * redis过期时间，以秒为单位，一小时
     */
    public final static int EXRP_HOUR = 60 * 60;

    /**
     * redis过期时间，以秒为单位，一天
     */
    public final static int EXRP_DAY = 60 * 60 * 24;

    /**
     * redis-key-前缀-shiro:perm_roles_rule:
     */
    public final static String ALL_URL_KEY = "shiro:all:url:";

    public final static  String USER_ID_KEY = "userId";
    public final static  String TENANT_ID_KEY = "tenantId";
    public final static String ADMIN_KEY = "isAdmin";
    public final static  String ROLE_ID_LIST_KEY = "roleIdList";
    public final static  String POSITION_ID_LIST_KEY = "positionIdList";
    public final static  String DEPARTMENT_ID_LIST_KEY = "departmentIdList";

    public final static  String USER_NAME_KEY = "user_name";

    public final static String CLIENT_ID_KEY = "client_id";

    public final static String LOGIN_TYPE = "login_type";

    public final static String GATEWAY_ROUTE_ATTR = "gatewayRoute";
    /**
     * JWT载体key
     */
    public final static  String JWT_PAYLOAD_KEY = "payload";
    /**
     * 认证请求头key
     */
    public final static  String AUTHORIZATION_KEY = "Authorization";
    /**
     * JWT载体key
     */
    public final static  String SCHEMAS_TENANT_ID = "schemasTenantId";


    /**
     * Basic认证前缀
     */
    public final static  String BASIC_PREFIX = "Basic ";


    /**
     * token前缀
     */
    public final static String AUTH_TOKEN_ACCESS_PREFIX = "auth:token:access:";

    /**
     * 登录图片验证码前缀
     */
    public final static String VERIFY_CODE_PREFIX = "redis:verifyCode:";

    /**
     * redis 发布订阅监听
     * websocket 接受消息
     */
    public final static String WEB_SOCKET_MESSAGE_EVENT = "message_event";


    /**
     * 已发送平台公告用户集合
     */
    public final static String PUBLIC_NOTICE_USER_LIST = "redis:public_notice_user_list:";
}
