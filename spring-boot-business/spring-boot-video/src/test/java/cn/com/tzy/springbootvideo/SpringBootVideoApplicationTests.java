package cn.com.tzy.springbootvideo;

import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.MqListener;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class SpringBootVideoApplicationTests {
    @Autowired
    private MqClient mqClient;
    @Autowired
    private MqListener mqListener;
    @Test
    void contextLoads() throws InterruptedException {
    }
}
