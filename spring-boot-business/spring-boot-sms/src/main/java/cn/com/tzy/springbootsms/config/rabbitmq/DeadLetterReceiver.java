package cn.com.tzy.springbootsms.config.rabbitmq;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 死信消息
 */
@Log4j2
@Component
@RabbitListener(bindings = {@QueueBinding(
    exchange = @Exchange(value = MqConstant.DEAD_LETTER_EXCHANGE,durable = "true"),
    value = @Queue(value = MqConstant.DEAD_LETTER_QUEUE,durable = "true"),
    key = MqConstant.DEAD_LETTER_ROUTING_KEY
)})
public class DeadLetterReceiver {

    @RabbitHandler
    public void onMessage(Object obj, Channel channel, Message message) throws IOException {
        log.info("死信队列[DeadLetterReceiver]接收到的消息为：MyAckReceiver  data:{}", obj);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
