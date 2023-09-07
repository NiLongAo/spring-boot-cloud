package cn.com.tzy.springbootsms.config.socket.qr.event;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootfeignsso.api.oauth.MiniServiceFeign;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRSendEvent;
import cn.com.tzy.springbootsms.config.socket.qr.common.WxMiniLoginData;
import cn.com.tzy.springbootsms.config.socket.qr.namespace.QRNamespace;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.TypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.bean.BeanUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        RestResult<?> result = oAuthUserServiceFeign.mini(appClientId, appClientSecret, TypeEnum.APP_WX_MINI.getType(), "wx_mini",
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
            String uuid = client.getSessionId().toString().replaceAll("-", "");
            NotNullMap data = new NotNullMap();
            data.put("mini_scene",uuid);
            data.put("data",build);
            //登录成功后存储
            String key = String.format("%s%s", WxMiniConstant.WX_MINI_LOGIN_SCENE, mini.getScene());
            RedisUtils.set(key,data,60*3);
        }else {
            client.sendEvent(QRSendEvent.OUT_LOGIN_EVENT,build);
        }
    }
}
