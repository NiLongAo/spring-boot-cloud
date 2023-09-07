package cn.com.tzy.springbootstarterstreamrabbitmq.config;

import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.AbstractMessageListener;
import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.MqListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Import({SimpleMessageConverter.class})
public class MQConfig<T> {

    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate rabbitTemplate;
    private final List<AbstractMessageListener>  abstractMessageListenerList;
    private final MessageConverter messageConverter;

    public MQConfig(ConnectionFactory connectionFactory,
                    RabbitTemplate rabbitTemplate,
                    ObjectProvider<AbstractMessageListener> abstractMessageListeners,
                    ObjectProvider<MessageConverter> messageConverters
                    ){
        this.connectionFactory =connectionFactory;
        this.rabbitTemplate = rabbitTemplate;
        this.abstractMessageListenerList = abstractMessageListeners.stream().collect(Collectors.toList());
        this.messageConverter = messageConverters.getIfAvailable();
    }


    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //设置忽略声明异常
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //确认模式 手动
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //消费者的最小数量
        container.setConcurrentConsumers(1);
        //消费者的最大数量
        container.setMaxConcurrentConsumers(20);
        //启动超时事件(3秒)
        container.setConsumerStartTimeout(3000L);
        //设置是否暴露监听器的通道
        container.setExposeListenerChannel(true);
        //设置在消息被拒绝时是否重新排队
        container.setDefaultRequeueRejected(false);
        // 重试失败处理 interval 尝试间隔  maxAttempts 尝试次数
        container.setRecoveryBackOff(new FixedBackOff(2000,3));
        //监听处理类
        container.setMessageListener(mqListener());
        return container;
    }

    @Bean
    public MqListener mqListener(){
        MqListener tMqListener = new MqListener();
        Map<String,AbstractMessageListener> map = new HashMap<>();
        if(abstractMessageListenerList != null && !abstractMessageListenerList.isEmpty()){
          for (AbstractMessageListener messageListener : abstractMessageListenerList) {
              map.put(messageListener.getKey(),messageListener);
          }
        }
        tMqListener.setAbstractMessageListeners(map);
        tMqListener.setMessageConverter(messageConverter);
        return tMqListener;
    }

    @Bean
    public MqClient mqUtils(){
        return new MqClient(rabbitAdmin(),rabbitTemplate,simpleMessageListenerContainer());
    }
}
