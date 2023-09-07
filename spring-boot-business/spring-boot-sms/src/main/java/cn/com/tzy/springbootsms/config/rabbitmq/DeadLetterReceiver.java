package cn.com.tzy.springbootsms.config.rabbitmq;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.AbstractMessageListener;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * 死信消息
 */
@Log4j2
@Component
public class DeadLetterReceiver extends AbstractMessageListener<HashMap<String,Object>> {

    @Resource
    private MessageConverter messageConverter;

    public DeadLetterReceiver() {
        super(MqConstant.DEAD_LETTER_EXCHANGE,MqConstant.DEAD_LETTER_ROUTING_KEY,MqConstant.DEAD_LETTER_QUEUE);
    }

    @Override
    public void onMessage(HashMap<String,Object>  message, Channel channel) {
        log.info("死信队列[DeadLetterReceiver]接收到的消息为：MyAckReceiver  data:{}", message);
    }
}
