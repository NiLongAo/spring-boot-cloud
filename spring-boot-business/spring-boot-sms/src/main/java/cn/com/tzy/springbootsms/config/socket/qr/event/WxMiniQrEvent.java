package cn.com.tzy.springbootsms.config.socket.qr.event;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootfeignsso.api.oauth.MiniServiceFeign;
import cn.com.tzy.springbootfeignsso.api.oauth.OAuthUserServiceFeign;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRData;
import cn.com.tzy.springbootsms.config.socket.qr.common.QRSendEvent;
import cn.com.tzy.springbootsms.config.socket.qr.namespace.QRNamespace;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.constant.WxMiniConstant;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class WxMiniQrEvent implements EventListener<QRData> {
    private final QRNamespace qrNamespace;
    @Value("${webApiClient.clientId}")
    private  String webClientId;
    @Value("${webApiClient.clientSecret}")
    private  String webClientSecret;

    @Resource
    private MiniServiceFeign ssoMiniServiceFeign;
    @Resource
    private OAuthUserServiceFeign oAuthUserServiceFeign;
    @Resource
    private cn.com.tzy.springbootfeignbean.api.bean.MiniServiceFeign beanMiniServiceFeign;

    public WxMiniQrEvent(QRNamespace qrNamespace) {
        this.qrNamespace = qrNamespace;
    }

    @Override
    public Class<QRData> getEventClass() {
        return QRData.class;
    }

    @Override
    public String getEventName() {
        return QRSendEvent.IN_LOGIN_QR_CODE_EVENT;
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
     * @param data　客户端发送数据{msgContent: msg}
     * @param request 请求信息
     */
    @Override
    public void onData(SocketIOClient client, QRData data, AckRequest request) throws IOException {
        //socket io发送
        String uuid = client.getSessionId().toString().replaceAll("-", "");
        RestResult<?> result = ssoMiniServiceFeign.getQRCode(uuid);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            QRData build = QRData.builder()
                    .code(QRData.Code.ERROR.getValue())
                    .message(result.getMessage())
                    .build();
            client.sendEvent(QRSendEvent.OUT_LOGIN_QR_CODE_EVENT, build);
            return;
        }
        String key = WxMiniConstant.WX_QRCODE_SCENE + uuid;
        Map<String, Object> map = BeanUtil.beanToMap(result.getData());
        map.put("type",data.getType());
        RedisUtils.set(key,map,3*60);
        QRData build = QRData.builder()
                .code(QRData.Code.SUCCESS.getValue())
                .message(QRData.Code.SUCCESS.getName())
                .data(map)
                .build();
        client.sendEvent(QRSendEvent.OUT_LOGIN_QR_CODE_EVENT,build);
    }

    /**
     * 发送登陆消息
     */
    public void sendLogin(QRData.Code code,String openId,String scene){
        if(code == QRData.Code.OVERDUE){
            QRData build = QRData.builder()
                    .code(code.getValue())
                    .message(code.getName())
                    .build();
            qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_INFO_EVENT,build);
            return;
        }
        RestResult<?> result = oAuthUserServiceFeign.miniWeb(webClientId, webClientSecret, LoginTypeEnum.WEB_WX_MINI.getType(), "wx_mini_web", openId);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            QRData build = QRData.builder()
                    .code(QRData.Code.ERROR.getValue())
                    .message(result.getMessage())
                    .build();
            qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_INFO_EVENT,build);
            return;
        }
        QRData build = QRData.builder()
                .code(QRData.Code.SUCCESS.getValue())
                .message(QRData.Code.SUCCESS.getName())
                .data(result.getData())
                .build();
        qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_INFO_EVENT,build);
        //处理是否有微信小程序登录
        String key = String.format("%s%s", WxMiniConstant.WX_MINI_LOGIN_SCENE, scene);
        if(RedisUtils.hasKey(key)){
            NotNullMap map = BeanUtil.toBean(RedisUtils.get(key), NotNullMap.class);
            String mini_scene = MapUtil.getStr(map, "mini_scene");
            QRData data = MapUtil.get(map, "data", QRData.class);
            qrNamespace.getNamespace().getRoomOperations(mini_scene).sendEvent(QRSendEvent.OUT_LOGIN_EVENT,data);
            RedisUtils.del(key);
        }
    }

    /**
     * 发送绑定消息
     */
    public void sendBind(QRData.Code code, String openId, String scene){
        if(code == QRData.Code.OVERDUE){
            QRData build = QRData.builder()
                    .code(code.getValue())
                    .message(code.getName())
                    .build();
            qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_BIND_EVENT,build);
            return;
        }

        Long userId = null;
        for (SocketIOClient client : qrNamespace.getNamespace().getRoomOperations(scene).getClients()) {
            Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
            List<String> strings = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
            if(strings.isEmpty()){
                log.info("客户端:" + client.getSessionId() + "未获取到 WxMiniQrEvent 连接");
            }
            Map<String, String> map = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, strings.get(0)).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
            userId = MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
            break;
        }
        log.info("发送绑定消息 userId:{}",userId);
        RestResult<?> result = beanMiniServiceFeign.saveMiniUser(MiniUserParam.builder()
                .userId(userId)
                .openId(openId)
                .build()
        );
        if(result.getCode() != RespCode.CODE_0.getValue()){
            QRData build = QRData.builder()
                    .code(QRData.Code.ERROR.getValue())
                    .message(result.getMessage())
                    .build();
            qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_BIND_EVENT,build);
            return;
        }
        QRData build = QRData.builder()
                .code(QRData.Code.SUCCESS.getValue())
                .message(result.getMessage())
                .data(result.getData())
                .build();
        qrNamespace.getNamespace().getRoomOperations(scene).sendEvent(QRSendEvent.OUT_LOGIN_BIND_EVENT,build);
        //微信小程序需要（后面优化小程序socket）
        String key =  String.format("%s%s", WxMiniConstant.WX_MINI_LOGIN_SCENE, scene);
        if(RedisUtils.hasKey(key)){
            NotNullMap map = BeanUtil.toBean(RedisUtils.get(key), NotNullMap.class);
            String mini_scene = MapUtil.getStr(map, "mini_scene");
            QRData data = MapUtil.get(map, "data", QRData.class);
            qrNamespace.getNamespace().getRoomOperations(mini_scene).sendEvent(QRSendEvent.OUT_LOGIN_EVENT,data);
            RedisUtils.del(key);
        }
    }
}
