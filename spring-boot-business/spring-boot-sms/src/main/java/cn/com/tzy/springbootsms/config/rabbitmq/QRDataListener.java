package cn.com.tzy.springbootsms.config.rabbitmq;

import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.event.WxMiniQrEvent;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstarterstreamrabbitmq.listenter.AbstractMessageListener;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class QRDataListener extends AbstractMessageListener<HashMap<String,Object>> {

    @Autowired
    private WxMiniQrEvent wxMiniQrEvent;

    public QRDataListener() {
        super(MqConstant.QR_EXCHANGE,MqConstant.QR_ROUTING_KEY,MqConstant.QR_QUEUE);
    }

    @Override
    public void onMessage(HashMap<String, Object> data, Channel channel) {
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
    }
}
