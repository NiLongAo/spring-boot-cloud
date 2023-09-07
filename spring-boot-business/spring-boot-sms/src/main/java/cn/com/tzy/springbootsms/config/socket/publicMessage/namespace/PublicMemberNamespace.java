package cn.com.tzy.springbootsms.config.socket.publicMessage.namespace;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootentity.vo.bean.UserInfoVo;
import cn.com.tzy.springbootfeignbean.api.bean.UserServiceFeign;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.hutool.core.bean.BeanUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.namespace.Namespace;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class PublicMemberNamespace implements NamespaceListener {
    /**
     * 空间名称
     */
    public static final String PUBLIC_MEMBER_NAMESPACE = "/public_member_namespace";

    @Autowired
    private UserServiceFeign userServiceFeign;

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
        String payload = client.getHandshakeData().getHttpHeaders().get(Constant.JWT_PAYLOAD_KEY);
        if(StringUtils.isEmpty(payload)){
            log.error("链接失效,未获取用户关键信息");
            return;
        }
        //以mac地址为key,SocketIOClient 为value存入map,后续可以指定mac地址向客户端发送消息
        Map map = null;
        try {
            map = (Map) AppUtils.decodeJson2(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()), Map.class);
        } catch (UnsupportedEncodingException e) {
            log.error("认证Json转换失败:",e);
            return;
        }
        Long userId = Long.valueOf(String.valueOf(map.get(Constant.USER_ID_KEY)));
        RestResult<?> result = userServiceFeign.getInfo(userId);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            log.error("未获取用户信息:{}",userId);
            return;
        }
        UserInfoVo userInfoVo = BeanUtil.toBean(result.getData(), UserInfoVo.class);
        //建立用户房间
        client.joinRoom(String.format("%s:%s",Constant.USER_ID_KEY,userInfoVo.getId()));
        //建立租户房间
        client.joinRoom(String.format("%s:%s",Constant.TENANT_ID_KEY,userInfoVo.getTenantId()));
        //建立角色房间
        userInfoVo.getRoleIdList().forEach(obj->client.joinRoom(String.format("%s:%s",Constant.ROLE_ID_LIST_KEY,obj)));
        //建立职位房间
        userInfoVo.getPositionIdList().forEach(obj->client.joinRoom(String.format("%s:%s",Constant.POSITION_ID_LIST_KEY,obj)));
        //建立部门房间
        userInfoVo.getDepartmentIdList().forEach(obj->client.joinRoom(String.format("%s:%s",Constant.DEPARTMENT_ID_LIST_KEY,obj)));
        log.info("客户端:" + client.getSessionId() + "已连接,userId="+userId);
    }

    /**
     * 客户端关闭连接时触发：前端js触发：socket.disconnect();
     * @param client
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        String payload = client.getHandshakeData().getHttpHeaders().get(Constant.JWT_PAYLOAD_KEY);
        if(StringUtils.isEmpty(payload)){
            client.disconnect();
            log.error("链接失效,未获取用户关键信息");
            return;
        }
        //以mac地址为key,SocketIOClient 为value存入map,后续可以指定mac地址向客户端发送消息
        Map map = null;
        try {
            map = AppUtils.decodeJson2(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()), Map.class);
        } catch (UnsupportedEncodingException e) {
            client.disconnect();
            log.error("认证Json转换失败:",e);
            return;
        }
        Long userId = Long.valueOf(String.valueOf(map.get(Constant.USER_ID_KEY)));
        RestResult<?> result = userServiceFeign.getInfo(userId);
        if(result.getCode() != RespCode.CODE_0.getValue()){
            log.error("未获取用户信息:{}",userId);
            return;
        }
        UserInfoVo userInfoVo = BeanUtil.toBean(result.getData(), UserInfoVo.class);

        //用户房间移除当前用户
        client.leaveRoom(String.format("%s:%s",Constant.USER_ID_KEY,userInfoVo.getId()));
        //租户房间移除当前用户
        client.leaveRoom(String.format("%s:%s",Constant.TENANT_ID_KEY,userInfoVo.getTenantId()));
        //角色房间移除当前用户
        userInfoVo.getRoleIdList().forEach(obj->client.leaveRoom(String.format("%s:%s",Constant.ROLE_ID_LIST_KEY,obj)));
        //职位房间移除当前用户
        userInfoVo.getPositionIdList().forEach(obj->client.leaveRoom(String.format("%s:%s",Constant.POSITION_ID_LIST_KEY,obj)));
        //部门房间移除当前用户
        userInfoVo.getDepartmentIdList().forEach(obj->client.leaveRoom(String.format("%s:%s",Constant.DEPARTMENT_ID_LIST_KEY,obj)));
        log.info("客户端:" + client.getSessionId() + "断开连接,userId="+userId);
    }

    public List<Long> getRoomId(String prefix){
        Namespace namespace = getNamespace();
        Set<String> rooms = namespace.getRooms();
        List<Long> collect = rooms.stream().filter(obj -> obj.startsWith(prefix)).map(obj -> Long.parseLong(obj.split(prefix)[1])).collect(Collectors.toList());
        return collect;
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
