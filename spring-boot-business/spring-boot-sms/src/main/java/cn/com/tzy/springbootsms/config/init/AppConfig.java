package cn.com.tzy.springbootsms.config.init;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Constructor >> @Autowired >> @PostConstruct  >> InitializingBean >> ApplicationRunner
 */
@Component
@Order(1)
@Getter
@Setter
public class AppConfig {

    public File appDir;
    public File tempDir;
    @Resource
    private ServletContext sc;
    @Resource
    private MqClient mqClient;
    private SocketNacosApplicationRunner socketNacosApplicationRunner;


    @SneakyThrows
    @PostConstruct
    public void init() {
        this.appDir = new File(sc.getRealPath(""));
        this.tempDir = new File(appDir, Constant.PATH_TEMP);
        //死信队列
        mqClient.binding(MqConstant.DEAD_LETTER_EXCHANGE,MqConstant.DEAD_LETTER_ROUTING_KEY,MqConstant.DEAD_LETTER_QUEUE, ExchangeTypes.DIRECT,false);
        //绑定扫码mq
        mqClient.binding(MqConstant.QR_EXCHANGE,MqConstant.QR_ROUTING_KEY,MqConstant.QR_QUEUE, ExchangeTypes.DIRECT,false,new HashMap<String,Object>(){{
            put("x-dead-letter-exchange", MqConstant.DEAD_LETTER_EXCHANGE);
            put("x-dead-letter-routing-key", MqConstant.DEAD_LETTER_ROUTING_KEY);
        }});
    }

    @PreDestroy
    public void destroy() throws NacosException {
        socketNacosApplicationRunner.destroy();
    }


}
