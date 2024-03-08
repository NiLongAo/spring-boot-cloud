package cn.com.tzy.springbootsms.config.socket.qr.event;

import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.springbootcomm.common.mq.MqConstant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.mq.QrRoutingModel;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRSendEvent;
import cn.com.tzy.springbootsms.config.socket.qr.common.WxMiniLoginData;
import cn.com.tzy.springbootsms.config.socket.qr.namespace.QRNamespace;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.springbootstarterstreamrabbitmq.config.MqClient;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class WxMiniLoginEvent implements EventListener<WxMiniLoginData> {
    private final QRNamespace qrNamespace;

    @Value("${appClient.clientId}")
    private  String appClientId;
    @Value("${appClient.clientSecret}")
    private  String appClientSecret;
    @Resource
    private OAuthUserServiceFeign oAuthUserServiceFeign;
    @Resource
    private MqClient mqClient;

    public WxMiniLoginEvent(QRNamespace qrNamespace) {
        this.qrNamespace = qrNamespace;
    }

    @Override
    public Class<WxMiniLoginData> getEventClass() {
        return WxMiniLoginData.class;
    }

    @Override
    public String getEventName() {
        return QRSendEvent.IN_LOGIN_EVENT;
    }


    @Override
    public NamespaceListener getNamespace() {
        return qrNamespace;
    }

    /**
     * 自定义消息事件，客户端js触发：socket.emit('messageevent', {msgContent: msg}); 时触发
     * 前端js的 socket.emit("事件名","参数数据")方法，是触发后端自定义消息事件的时候使用的,
     * 前端js的 socket.on("事件名",匿名函数(服务器向客户端发送的数据))为监听服务器端的事件
     * @param client　客户端信息
     * @param mini　客户端发送数据{msgContent: msg}
     * @param request 请求信息
     */
    @Override
    public void onData(SocketIOClient client, WxMiniLoginData mini, AckRequest request) throws IOException {
        RestResult<?> result = oAuthUserServiceFeign.mini(appClientId, appClientSecret, LoginTypeEnum.APP_WX_MINI.getType(), "wx_mini",
                mini.getCode(),
                mini.getSessionKey(),
                mini.getEncryptedData(),
                mini.getSignature(),
                mini.getIv(),
                mini.getRawData(),
                mini.getScene()
        );
        if(result.getCode() != RespCode.CODE_0.getValue()){
            QRData build = QRData.builder()
                    .code(QRData.Code.ERROR.getValue())
                    .message(result.getMessage())
                    .build();
            client.sendEvent(QRSendEvent.OUT_LOGIN_EVENT, build);
            return;
        }
        QRData build = QRData.builder()
                .code(QRData.Code.SUCCESS.getValue())
                .message(QRData.Code.SUCCESS.getName())
                .data(result.getData())
                .build();

        if(StringUtils.isNotEmpty(mini.getScene())){
            //缓存登录成功信息
            String uuid = client.getSessionId().toString().replaceAll("-", "");
            //登录成功后存储
            String key = String.format("%s%s", WxMiniConstant.WX_MINI_LOGIN_SCENE, mini.getScene());
            RedisUtils.set(key,new HashMap<String,Object>(){{
                put("mini_scene",uuid);
                put("data",build);
            }},60*3);
            //发送Mq通知
            Map<String, Object> map = BeanUtil.beanToMap(result.getData());
            String openId = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, MapUtil.getStr(map, "access_token")).buildNameValue(JwtCommon.JWT_USER_NAME, false);
            //发送mq消息
            mqClient.send(MqConstant.QR_EXCHANGE,MqConstant.QR_ROUTING_KEY, QrRoutingModel.builder().scene(mini.getScene()).openId(openId).build());
        }else {
            client.sendEvent(QRSendEvent.OUT_LOGIN_EVENT,build);
        }
    }
}
