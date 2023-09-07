package cn.com.tzy.springbootsms.config.socket.qr.common;

/**
 * 二维码发送事件
 */
public class QRSendEvent {
    /**
     * app登录事件
     */
    public static final String IN_LOGIN_EVENT = "in_login_event";

    /**
     *  app返回登录信息事件
     */
    public static final String OUT_LOGIN_EVENT = "out_login_event";

    /**
     * web获取二维码信息
     */
    public static final String IN_LOGIN_QR_CODE_EVENT = "in_login_qr_code_event";
    /**
     * web发送二维码信息
     */
    public static final String OUT_LOGIN_QR_CODE_EVENT = "out_login_qr_code_event";
    /**
     * web发送登陆信息
     */
    public static final String OUT_LOGIN_INFO_EVENT = "out_login_info_event";
    /**
     * web发送绑定信息
     */
    public static final String OUT_LOGIN_BIND_EVENT = "out_login_bind_event";

}
