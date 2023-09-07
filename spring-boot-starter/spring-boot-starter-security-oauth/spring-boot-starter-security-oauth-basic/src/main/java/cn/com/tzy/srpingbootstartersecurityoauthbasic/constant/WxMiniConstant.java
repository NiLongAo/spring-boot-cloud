package cn.com.tzy.srpingbootstartersecurityoauthbasic.constant;

public class WxMiniConstant {

    /**
     * 微信小程序码rdeis标识
     */
    public static final String WX_MINI_LOGIN_SCENE = "wx:mini:login:scene:";
    /**
     * 微信小程序码rdeis标识
     */
    public static final String WX_QRCODE_SCENE = "wx:qrcode:scene:";
    /**
     * 微信小程序认证类型
     */
    public static final String WX_MINI_TYPE = "wx_mini";

    /**
     * 微信授权传入code
     */
    public static final String MINI_CODE = "code";
    /**
     * 获取用户信息参数
     * sessionKey
     */
    public static final String MINI_SESSION_KEY = "sessionKey";
    /**
     * 获取用户信息参数
     * 加密串
     */
    public static final String MINI_ENCRYPTED_DATA = "encryptedData";
    /**
     * 获取用户信息参数
     * 加密串
     */
    public static final String MINI_SCENE = "scene";

    /**
     * 获取用户信息参数
     * 偏移量
     */
    public static final String MINI_VI = "iv";

    /**
     * 获取用户信息参数
     * 加密串
     */
    public static final String MINI_RAW_DATA = "rawData";

    /**
     * 获取用户信息参数
     * 加密串
     */
    public static final String MINI_SIGNATURE = "signature";

    /**
     * 验证码登录请求参数：登录地址
     */
    public static final String SPRING_SECURITY_RESTFUL_LOGIN_URL = "/form/wx_mini_login";

}
