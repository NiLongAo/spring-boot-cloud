package cn.com.tzy.springbootsms.config.socket.publicMessage.namespace;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.srpingbootstartersecurityoauthbasic.common.LoginTypeEnum;
import cn.hutool.core.map.MapUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.namespace.Namespace;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
public class PublicMemberNamespace implements NamespaceListener {
    /**
     * 空间名称
     */
    public static final String PUBLIC_MEMBER_NAMESPACE = "/public_member_namespace";
    private SocketIOServer socketIOServer;

    @Override
    public String getNamespaceName() {
        return PUBLIC_MEMBER_NAMESPACE;
    }

    /**
     * 客户端连接的时候触发，前端js触发：socket = io.connect("http://192.168.9.209:9092");
     * @param client
     */
    @Override
    public void onConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        List<String> strings = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
        if(strings.isEmpty()){
            log.info("客户端:" + client.getSessionId() + "未获取到 PublicMemberNamespace 连接");
        }
        Map<String, String> map = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, strings.get(0)).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        Long userId = MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
        String loginType = MapUtil.getStr(map, JwtCommon.JWT_LOGIN_TYPE);
        //建立用户房间
        client.joinRoom(String.format("%s:%s", LoginTypeEnum.getClientType(loginType).getUserType(),userId));
        log.info("客户端:" + client.getSessionId() + "已连接,userId="+userId);
    }

    /**
     * 客户端关闭连接时触发：前端js触发：socket.disconnect();
     * @param client
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        List<String> strings = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
        if(strings.isEmpty()){
            log.info("客户端:" + client.getSessionId() + "未获取到 PublicMemberNamespace 连接");
        }
        Map<String, String> map = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, strings.get(0)).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        Long userId = MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
        String loginType = MapUtil.getStr(map, JwtCommon.JWT_LOGIN_TYPE);
        //用户房间移除当前用户
        client.leaveRoom(String.format("%s:%s", LoginTypeEnum.getClientType(loginType).getUserType(),userId));
        log.info("客户端:" + client.getSessionId() + "断开连接,userId="+userId);
    }

    public List<String> findAllRoom(){
        Set<String> rooms = getNamespace().getRooms();
       return new ArrayList<>(rooms);
    }

    public Namespace getNamespace(){
        return  (Namespace)socketIOServer.getNamespace(PUBLIC_MEMBER_NAMESPACE);
    }

    @Override
    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    @Override
    public void setSocketIOServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }
}
