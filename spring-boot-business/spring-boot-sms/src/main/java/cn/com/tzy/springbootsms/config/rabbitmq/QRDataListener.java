package cn.com.tzy.springbootsms.config.rabbitmq;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.event.WxMiniQrEvent;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RabbitListener(bindings = {@QueueBinding(
    exchange = @Exchange(value = MqConstant.QR_EXCHANGE,durable = "true"),
    value = @Queue(value = MqConstant.QR_QUEUE,durable = "true"),
    key = MqConstant.QR_ROUTING_KEY
)})
public class QRDataListener {

    @Resource
    private WxMiniQrEvent wxMiniQrEvent;


    @RabbitHandler
    public void onMessage(Object obj, Channel channel, Message message) throws IOException {
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();
        converter.setDelegates(new HashMap<String, MessageConverter>(){{
            put("application/json",new Jackson2JsonMessageConverter());
        }});
        Object o1 = converter.fromMessage(message);

        Map<String, Object> data = BeanUtil.beanToMap(o1);
        log.info("QRDataListener data:{}",data);
        String scene = MapUtil.getStr(data, "scene");
        String openId = MapUtil.getStr(data, "openId");
        String key = WxMiniConstant.WX_QRCODE_SCENE + scene;
        Map map = new HashMap();
        if(RedisUtils.hasKey(key)){
            map = (Map)RedisUtils.get(key);
        }else {
            wxMiniQrEvent.sendLogin(QRData.Code.OVERDUE,scene,openId);
            wxMiniQrEvent.sendBind(QRData.Code.OVERDUE,openId,scene);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        Integer type = MapUtil.getInt(map, "type");
        QRData.QRType qrType = QRData.QRType.map.get(type);
        switch (qrType){
            case WX_MINI_QR_LOGIN:
                wxMiniQrEvent.sendLogin(QRData.Code.SUCCESS, scene, openId);
                break;
            case WX_MINI_QR_BIND:
                wxMiniQrEvent.sendBind(QRData.Code.SUCCESS,openId,scene);
                break;
            default:
                log.error("LoginQREvent,未获取任何有效信息：data:{}",data);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
