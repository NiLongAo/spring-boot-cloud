package cn.com.tzy.springbootstarterstreamrabbitmq.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Map;

@Log4j2
public class MqClient {

    private final RabbitAdmin rabbitAdmin;


    public MqClient(ConnectionFactory connectionFactory, MessageConverter messageConverter){
        this.rabbitAdmin =new RabbitAdmin(connectionFactory);
        rabbitAdmin.getRabbitTemplate().setMessageConverter(messageConverter);
    }

    public Binding binding(String exchangeName,String routingKey,String queueName,String type,boolean isExchange){
        return this.binding(createExchange(exchangeName, type, isExchange),addQueue(queueName),routingKey);
    }

    public Binding binding(String exchangeName,String routingKey,String queueName, String type,boolean isExchange,Map<String, Object> headerValues){
        return this.binding(createExchange(exchangeName, type, isExchange), addQueue(queueName),routingKey,headerValues);
    }

    /**
     * 创建交换器
     * @param exchangeName      交换机名词
     * @param type              ExchangeTypes.class
     * @param isExchange        是否延迟队列
     * @return
     */
    private Exchange createExchange(String exchangeName,String type,boolean isExchange) {
        Exchange build = null;
        if(isExchange){
            build = new ExchangeBuilder(exchangeName, type).delayed().durable(true).build();
        }else{
            build = new ExchangeBuilder(exchangeName, type).durable(true).build();
        }
        return build;
    }

    /**
     *
     * @return Queue
     */
    /**
     * 创建一个指定的Queue
     * @param queueName 队列名词
     * @return
     */
    private Queue addQueue(String queueName) {
        return new Queue(queueName,true,false,false);
    }

    /**
     * 绑定交换机与队列在mq中
     */
    private Binding binding(Exchange directExchange,Queue queue){
      return  binding(directExchange,queue,"");
    }

    /**
     * 绑定交换机与队列在mq中
     */
    private Binding binding(Exchange directExchange,Queue queue,String routingKey){
        rabbitAdmin.declareExchange(directExchange);
        log.info("声明交换机：{}",directExchange.getName());
        rabbitAdmin.declareQueue(queue);
        log.info("声明消息队列：{}",queue.getName());
        Binding with = BindingBuilder.bind(queue).to(directExchange).with(routingKey).noargs();
        rabbitAdmin.declareBinding(with);
        log.info("声明交换机与消息队列绑定关系,交换机：{},routingKey：{},消息队列：{}",directExchange.getName(),routingKey,queue.getName());
        return with;
    }

    /**
     * (只针对与HeadersExchange 交换机)
     * 绑定交换机与队列在mq中
     */
    private Binding binding(Exchange directExchange, Queue queue,String routingKey, Map<String, Object> headerValues){
        if(headerValues != null && !headerValues.isEmpty()){
            Map<String, Object> arguments = queue.getArguments();
            arguments.putAll(headerValues);
        }
        rabbitAdmin.declareExchange(directExchange);
        log.info("声明交换机：{}",directExchange.getName());
        rabbitAdmin.declareQueue(queue);
        log.info("声明消息队列：{}",queue.getName());
        Binding with = BindingBuilder.bind(queue).to(directExchange).with(routingKey).and(headerValues);
        rabbitAdmin.declareBinding(with);
        log.info("声明交换机与消息队列绑定关系,交换机：{},routingKey：{},消息队列：{}",directExchange.getName(),routingKey,queue.getName());
        return with;
    }

    /**
     * 去掉一个binding
     */
    public void removeBinding(String exchangeName,String routingKey,String queueName) {
        removeBinding(new Binding(queueName, Binding.DestinationType.QUEUE,exchangeName,routingKey,null));
    }

    /**
     * 去掉一个binding
     * @param binding
     */
    public void removeBinding(Binding binding) {
        rabbitAdmin.removeBinding(binding);
    }

    /**
     * 删除交换机
     * @param exchange
     */
    public void deleteExchange(String exchange) {
        rabbitAdmin.deleteExchange(exchange);
    }

    /**
     * 删除通道
     * @param queue
     */
    public void deleteQueue(String queue) {
        rabbitAdmin.deleteQueue(queue);
    }

    /**
     * 发送消息
     * @param exchangeName 交换机名称
     * @param routingKey 路由名称
     * @param msg 消息主体
     */
    public void send(String exchangeName,String routingKey,Object msg){
        rabbitAdmin.getRabbitTemplate().convertAndSend(exchangeName, routingKey, msg);
    }
    /**
     * 发送延迟消息
     * 注意消息队列服务器添加延迟消息队列组件后才可使用
     * @param exchangeName 交换机名称
     * @param queueName 消息队列名称
     * @param msg 消息主体
     * @param expiration 延迟时间(毫秒)
     */
    public void sendDelay(String exchangeName,String queueName,Object msg,Integer expiration){
        rabbitAdmin.getRabbitTemplate().convertAndSend(exchangeName, queueName, msg,message -> {
            message.getMessageProperties().setDelay(expiration);
            return message;
        });
    }
}
