package cn.com.tzy.springbootstarterstreamrabbitmq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import({SimpleMessageConverter.class})
public class MQConfig<T> {

    private final ConnectionFactory connectionFactory;

    public MQConfig(ConnectionFactory connectionFactory){
        this.connectionFactory =connectionFactory;
    }

    @Bean
    public MqClient mqClient(){
        return new MqClient(connectionFactory);
    }
}
