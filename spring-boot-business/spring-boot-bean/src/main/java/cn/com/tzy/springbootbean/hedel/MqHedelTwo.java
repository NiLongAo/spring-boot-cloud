// package cn.com.tzy.springbootbean.hedel;
//
// import cn.com.tzy.springbootcomm.constant.Constant;
// import cn.com.tzy.springbootentity.dome.bean.User;
// import com.rabbitmq.client.Channel;
// import lombok.extern.log4j.Log4j2;
// import org.apache.commons.lang.time.DateFormatUtils;
// import org.springframework.amqp.core.ExchangeTypes;
// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.rabbit.annotation.*;
// import org.springframework.stereotype.Component;
//
// import java.io.IOException;
// import java.util.Date;
//
// @RabbitListener(bindings = @QueueBinding(
//         value = @Queue(value = "testQueueName1",declare = "false",autoDelete = "true"),
//         exchange = @Exchange(value = "testExchange",type = ExchangeTypes.DIRECT),
//         key = "testkey"
// ))
// @Log4j2
// @Component
// public class MqHedelTwo {
//
//     @RabbitHandler
//     public void test(Message message, Channel channel, User user) throws IOException {
//         log.info( "发送结束时间2：{}" , DateFormatUtils.format(new Date(), Constant.DATE_TIME_FORMAT));
//         log.info(user.toString());
//         channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//     }
// }
