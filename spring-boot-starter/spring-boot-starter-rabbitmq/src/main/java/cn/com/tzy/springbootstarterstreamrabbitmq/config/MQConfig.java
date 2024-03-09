package cn.com.tzy.springbootstarterstreamrabbitmq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;

@Configuration
@Import({SimpleMessageConverter.class})
public class MQConfig<T> {

    private final ConnectionFactory connectionFactory;


    public MQConfig(ConnectionFactory connectionFactory){
        this.connectionFactory =connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //设置忽略声明异常
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        rabbitAdmin.getRabbitTemplate().setMessageConverter(jsonMessageConverter());
        return rabbitAdmin;
    }

//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        //确认模式 手动
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        //消费者的最小数量
//        container.setConcurrentConsumers(1);
//        //消费者的最大数量
//        container.setMaxConcurrentConsumers(20);
//        //启动超时事件(3秒)
//        container.setConsumerStartTimeout(3000L);
//        //设置是否暴露监听器的通道
//        container.setExposeListenerChannel(true);
//        //设置在消息被拒绝时是否重新排队
//        container.setDefaultRequeueRejected(false);
//        // 重试失败处理 interval 尝试间隔  maxAttempts 尝试次数
//        container.setRecoveryBackOff(new FixedBackOff(2000,3));
//        container.setMessageListener();
//        return container;
//    }

    @Bean
    public MqClient mqClient(){
        return new MqClient(rabbitAdmin());
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();
        converter.setDelegates(new HashMap<String, MessageConverter>(){{
            put("application/json",new Jackson2JsonMessageConverter());
        }});
        return converter;
    }


}
