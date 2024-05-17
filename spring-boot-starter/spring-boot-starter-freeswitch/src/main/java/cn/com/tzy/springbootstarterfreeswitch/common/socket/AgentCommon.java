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
     * 客服请求推流地址
     */
    public static final String AGENT_IN_PUSH_PATH = "AGENT:IN:PUSH_PATH";
    /**
     *
     * 客服回调推流地址
     */
    public static final String AGENT_OUT_PUSH_PATH = "AGENT:OUT:PUSH_PATH";
    /**
     *
     * 客服回调推流地址
     */
    public static final String AGENT_OUT_PUSH_PATH_OK = "AGENT:OUT:PUSH_PATH_OK";
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
     * 推流回调
     */
    public static final String AGENT_OUT_CALL_PHONE = "AGENT:OUT:CALL_PHONE";
}
