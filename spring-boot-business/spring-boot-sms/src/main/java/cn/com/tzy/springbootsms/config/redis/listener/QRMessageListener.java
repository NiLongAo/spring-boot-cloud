//package cn.com.tzy.springbootsms.config.redis.listener;
//
//import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
//import cn.com.tzy.springbootsms.config.socket.qr.common.QRMessage;
//import cn.com.tzy.springbootsms.config.socket.qr.event.WxMiniQrBindEvent;
//import cn.com.tzy.springbootsms.config.socket.qr.event.WxMiniQrLoginEvent;
//import cn.com.tzy.springbootstarterredis.common.RedisCommon;
//import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
//import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
//import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
//import cn.hutool.core.map.MapUtil;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * redis 接收订阅消息
// * redis发布消息 redisTemplate.convertAndSend(RedisCommon.WEB_REDIS_MESSAGE_EVENT,AppUtils.encodeJson(inMessage));
// */
//@Log4j2
//@Component
//public class QRMessageListener extends AbstractMessageListener {
//
//    @Autowired
//    private WxMiniQrLoginEvent wxMiniQrLoginEvent;
//    @Autowired
//    private WxMiniQrBindEvent wxMiniQrBindEvent;
//
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    public QRMessageListener() {
//        super(RedisCommon.Q_R_EVENT);
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] bytes) {
//        String msg = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());
//        Map data = JSONObject.parseObject(msg, Map.class);
//        String scene = MapUtil.getStr(data, "scene");
//        String openId = MapUtil.getStr(data, "openId");
//        String key = WxMiniConstant.WX_QRCODE_SCENE + scene;
//        Map map = new HashMap();
//        if(RedisUtils.hasKey(key)){
//            map = (Map)RedisUtils.get(key);
//        }else {
//            wxMiniQrLoginEvent.send(QRMessage.Code.OVERDUE,scene,openId);
//            wxMiniQrBindEvent.send(QRMessage.Code.OVERDUE,openId,scene,map);
//            return;
//        }
//        Integer type = MapUtil.getInt(map, "type");
//        QRData.QRType qrType = QRData.QRType.map.get(type);
//        switch (qrType){
//            case WX_MINI_QR_LOGIN:
//                wxMiniQrLoginEvent.send(QRMessage.Code.SUCCESS, scene, openId);
//                break;
//            case WX_MINI_QR_BIND:
//                wxMiniQrBindEvent.send(QRMessage.Code.SUCCESS,openId,scene,map);
//                break;
//            default:
//                log.error("LoginQREvent,未获取任何有效信息：data:{}",data);
//        }
//    }
//}
