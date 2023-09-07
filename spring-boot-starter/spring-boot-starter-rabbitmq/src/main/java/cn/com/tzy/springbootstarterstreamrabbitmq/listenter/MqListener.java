package cn.com.tzy.springbootstarterstreamrabbitmq.listenter;

import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * mq消息统一回调处理处理
 */
@Log4j2
public  class  MqListener implements ChannelAwareMessageListener {
    private MessageConverter messageConverter;

    private volatile Map<String, AbstractMessageListener> abstractMessageListeners = new HashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        String exchange = messageProperties.getReceivedExchange();//交换机名称
        String routingKey = messageProperties.getReceivedRoutingKey();//规则key
        String queue = message.getMessageProperties().getConsumerQueue();//通道名
        long deliveryTag = messageProperties.getDeliveryTag();//传送标记
        log.warn("交换机名称:{},规则key:{},通道名:{},传送标记:{},",exchange,routingKey,queue,deliveryTag);
        try {
            String key = String.format("%s_%s_%s", exchange, routingKey, queue);
            AbstractMessageListener abstractMessageListener = abstractMessageListeners.get(key);
            if(abstractMessageListener == null){
                log.error("Mq获取回调消息错误，未获取到处理方法:key:{}",key);
                channel.basicAck(deliveryTag, false);
                return;
            }
            Object obj = messageConverter.fromMessage(message);
            abstractMessageListener.onMessage(obj, channel);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("[MQ错误消息],重新放回队列， Exception:",e);
            channel.basicNack(deliveryTag, false, true);
        }
    }


    public void setAbstractMessageListeners(Map<String, AbstractMessageListener> abstractMessageListeners) {
        this.abstractMessageListeners = abstractMessageListeners;
    }
    public void add(AbstractMessageListener ... abstractMessageListener){
        for (AbstractMessageListener messageListener : abstractMessageListener) {
            abstractMessageListeners.put(messageListener.getKey(),messageListener);
        }
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    public void delKey(String key){
        abstractMessageListeners.remove(key);
    }
}
