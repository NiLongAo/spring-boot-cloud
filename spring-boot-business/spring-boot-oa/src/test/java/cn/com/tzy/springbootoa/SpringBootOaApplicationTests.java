package cn.com.tzy.springbootoa;

import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
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

  @Test
  public void contextLoads() throws InterruptedException {

  }
}
