package cn.com.tzy.springbootstarterstreamrabbitmq.config;

import cn.com.tzy.springbootstarterredis.pool.RedisPool;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig<T> {

    private final ConnectionFactory connectionFactory;

    public MQConfig(ConnectionFactory connectionFactory){
        this.connectionFactory =connectionFactory;
    }

    @Bean
    public MqClient mqClient(){
        return new MqClient(connectionFactory,jsonMessageConverter());
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 临时设置 MessageConverter 为 Jackson2JsonMessageConverter
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(RedisPool.objectMapper(null));
    }


}
