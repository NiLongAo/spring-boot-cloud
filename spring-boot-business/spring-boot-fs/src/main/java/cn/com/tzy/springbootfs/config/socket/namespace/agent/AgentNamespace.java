package cn.com.tzy.springbootfs.config.socket.namespace.agent;

import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import cn.hutool.core.map.MapUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.namespace.Namespace;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class AgentNamespace implements NamespaceListener {
    /**
     * 空间名称
     */

    private SocketIOServer socketIOServer;
    @Resource
    private AgentService agentService;
    @Override
    public String getNamespaceName() {return AgentCommon.SOCKET_AGENT;}
    /**
     * 客户端连接的时候触发，前端js触发：socket = io.connect("http://192.168.9.209:9092");
     * 连接后直接登陆
     * @param client
     */
    @Override
    public void onConnect(SocketIOClient client) {
        log.info("客户端:" + client.getSessionId() + "已连接");
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        List<String> strings = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
        if(strings.isEmpty()){
            log.error("客户端:" + client.getSessionId() + "未获取到 PublicMemberNamespace 连接");
            return;
        }
        Map<String, String> map = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, strings.get(0)).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        Long userId = MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
        Agent agent = agentService.findUserId(userId);
        if(agent == null){
            log.error("客户端:{} 用户：{} 未获取到 客服信息",client.getSessionId(),userId);
            return;
        }
        //建立客服房间
        client.joinRoom(getSocketAgentKey(agent.getAgentCode()));
        RedisService.getAgentInfoManager().putAgentCode(client.getSessionId().toString(),agent.getAgentCode());
        agentService.login(agent.getAgentCode(),(data)->{
            client.sendEvent(AgentCommon.AGENT_OUT_LOGIN, RestResult.result(data.getCode(),data.getMessage(),data.getData()));
        });
    }

    /**
     * 客户端关闭连接时触发：前端js触发：socket.disconnect();
     * 移除后退出登陆客服
     * @param client
     */
    @Override
    public void onDisconnect(SocketIOClient client) {
        log.info("客户端:" + client.getSessionId() + "断开连接");
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        List<String> strings = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
        if(strings.isEmpty()){
            log.error("客户端:" + client.getSessionId() + "未获取到 PublicMemberNamespace 连接");
            return;
        }
        Map<String, String> map = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, strings.get(0)).setPrefix(JwtCommon.AUTHORIZATION_PREFIX).builderJwtUser( null);;
        Long userId = MapUtil.getLong(map, JwtCommon.JWT_USER_ID);
        Agent agent = agentService.findUserId(userId);
        if(agent == null){
            log.error("客户端:{} 用户：{} 未获取到 客服信息",client.getSessionId(),userId);
            return;
        }
        //移除
        client.leaveRoom(getSocketAgentKey(agent.getAgentCode()));
        RedisService.getAgentInfoManager().delAgentCode(client.getSessionId().toString());
        agentService.logout(agent.getAgentCode());
    }

    @Override
    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    @Override
    public void setSocketIOServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public Namespace getNamespace(){
        return  (Namespace)socketIOServer.getNamespace(AgentCommon.SOCKET_AGENT);
    }

    public String getSocketAgentKey(String agentCode){
        return String.format("%s:%s", AgentCommon.SOCKET_AGENT,agentCode);
    }
}
