package cn.com.tzy.springbootstarterfreeswitch.common.socket;

/**
 * 客服相关事件
 * 连接时自动登陆
 */
public class AgentCommon {

    /**
     * 客服socket前缀
     */
    public static final String SOCKET_AGENT = "/socket_agent";
    /**
     *
     * 登陆回调回调
     */
    public static final String AGENT_OUT_LOGIN = "AGENT:OUT:LOGIN";
    /**
     *
     * 客服回调推流地址
     */
    public static final String AGENT_OUT_PUSH_PATH = "AGENT:OUT:PUSH_PATH";
    /**
     *
     * 客服推流关闭
     */
    public static final String AGENT_OUT_PUSH_PATH_LOGOUT = "AGENT:OUT:PUSH_PATH_LOGOUT";
    /**
     *
     * 客服拨打电话
     */
    public static final String AGENT_IN_CALL_PHONE = "AGENT:IN:CALL_PHONE";
    /**
     *
     * 客服挂断电话
     */
    public static final String AGENT_IN_HANG_UP_PHONE = "AGENT:IN:HANG_UP_PHONE";
    /**
     *
     * 客服挂断电话回调
     */
    public static final String AGENT_OUT_HANG_UP_PHONE = "AGENT:OUT:HANG_UP_PHONE";
    /**
     *
     * 客服拨打电话回调
     */
    public static final String AGENT_OUT_CALL_PHONE = "AGENT:OUT:CALL_PHONE";

    /**
     *
     * 客服电话通知处理
     */
    public static final String AGENT_OUT_CALL_NOTIFICATION = "AGENT:OUT:PHONE_NOTIFICATION";
    /**
     * 客服状态更变
     */
    public static final String AGENT_IN_STATUS = "AGENT:IN:STATUS";
    /**
     * 客服状态更变通知
     */
    public static final String AGENT_OUT_STATUS = "AGENT:OUT:STATUS";


    /**
     * 客服来电操作
     */
    public static final String AGENT_IN_ANSWER_PHONE = "AGENT:IN:ANSWER_PHONE";
    /**
     * 客服来电操作
     */
    public static final String AGENT_OUT_ANSWER_PHONE = "AGENT:OUT:ANSWER_PHONE";
}
