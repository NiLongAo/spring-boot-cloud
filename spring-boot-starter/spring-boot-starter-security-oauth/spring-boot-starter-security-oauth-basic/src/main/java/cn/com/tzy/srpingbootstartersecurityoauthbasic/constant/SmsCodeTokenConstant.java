package cn.com.tzy.srpingbootstartersecurityoauthbasic.constant;

public class SmsCodeTokenConstant {

    /**
     * 短信验证类型
     */
    public static final String GRANT_TYPE = "sms";
    /**
     * 验证码登录请求参数：手机号码
     */
    public static final String SPRING_SECURITY_RESTFUL_PHONE_KEY = "phone";
    /**
     * 验证码登录请求参数：短信验证码
     */
    public static final String SPRING_SECURITY_RESTFUL_VERIFY_CODE_KEY = "SmsCodeCode";

    /**
     * 验证码前缀
     */
    public final static String VERIFICATION_CODE_PREFIX = "redis:verificationCode:";


    /**
     * 验证码登录请求参数：登录地址
     */
    public static final String SPRING_SECURITY_RESTFUL_LOGIN_URL = "/form/phone_login";
}
