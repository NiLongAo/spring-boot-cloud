package cn.com.tzy.springbootoa;

import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.MqListener;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class SpringBootOaApplicationTests {

  /**
   * mq 发送消息 与发送延迟消息队列测试
   */
  @Autowired
  private MqClient mqClient;
  @Autowired
  private MqListener mqListener;

  @Test
  public void contextLoads() throws InterruptedException {
//    String testExchange = "testExchange";
//    String testExchange1 = "testExchange1";
//    String routingKey = "routingKey";
//    String routingKey1 = "routingKey1";
//    String testQueueName = "testQueueName";
//    String testQueueName1 = "testQueueName1";
//
//    AbstractMessageListener<User> abstractMessageListener1 = new AbstractMessageListener<User>(testExchange,routingKey,testQueueName) {
//      @Override
//      public void onMessage(User message, Channel channel) {
//        log.info( "发送结束时间1：{}" , DateFormatUtils.format(new Date(), Constant.DATE_TIME_FORMAT));
//        log.info(message.toString());
//      }
//    };
//    AbstractMessageListener<User> abstractMessageListener2 = new AbstractMessageListener<User>(testExchange,routingKey1,testQueueName) {
//      @Override
//      public void onMessage(User message, Channel channel) {
//        log.info( "发送结束时间2：{}" ,DateFormatUtils.format(new Date(), Constant.DATE_TIME_FORMAT));
//        log.info(message.toString());
//      }
//    };
//    //添加绑定
//    mqListener.add(abstractMessageListener1,abstractMessageListener2);
//    mqClient.binding(testExchange,routingKey,testQueueName, ExchangeTypes.DIRECT,false);
//    mqClient.binding(testExchange, routingKey1, testQueueName, ExchangeTypes.DIRECT, false);
//     while (true){
//       log.info("发送开始时间：{}" ,DateFormatUtils.format(new Date(), Constant.DATE_TIME_FORMAT));
//       mqClient.send(testExchange,routingKey1, User.builder().idCard("dfasdfadhgerfasdf").build());
//       Thread.sleep(4000);
//     }
  }
}
