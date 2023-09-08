package cn.com.tzy.springbootcomm.common.mq;

/**
 * @author TZY
 */
public class MqConstant {

    //**********************************sms服务相关key******************************************************
    /**
     * 扫码交换机死信队列
     */
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead_letter_routing_key";
    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";

    /**
     * 扫码交换机
     */
    public static final String QR_EXCHANGE = "qr_exchange";
    public static final String QR_ROUTING_KEY = "qr_routing_key";
    public static final String QR_QUEUE = "qr_queue";

}
