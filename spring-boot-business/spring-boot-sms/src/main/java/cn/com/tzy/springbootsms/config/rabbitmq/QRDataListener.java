package cn.com.tzy.springbootsms.config.rabbitmq;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootentity.mq.QrRoutingModel;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.event.WxMiniQrEvent;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
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
    public void onMessage(QrRoutingModel model, Channel channel, Message message) throws IOException {
        log.info("QRDataListener data:{}",model);
        String key = WxMiniConstant.WX_QRCODE_SCENE + model.getScene();
        Map map = new HashMap();
        if(RedisUtils.hasKey(key)){
            map = (Map)RedisUtils.get(key);
        }else {
            wxMiniQrEvent.sendLogin(QRData.Code.OVERDUE,model.getOpenId(),model.getScene());
            wxMiniQrEvent.sendBind(QRData.Code.OVERDUE,model.getOpenId(),model.getScene());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        Integer type = MapUtil.getInt(map, "type");
        QRData.QRType qrType = QRData.QRType.map.get(type);
        switch (qrType){
            case WX_MINI_QR_LOGIN:
                wxMiniQrEvent.sendLogin(QRData.Code.SUCCESS, model.getOpenId(),model.getScene());
                break;
            case WX_MINI_QR_BIND:
                wxMiniQrEvent.sendBind(QRData.Code.SUCCESS,model.getOpenId(),model.getScene());
                break;
            default:
                log.error("LoginQREvent,未获取任何有效信息：data:{}",model);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
