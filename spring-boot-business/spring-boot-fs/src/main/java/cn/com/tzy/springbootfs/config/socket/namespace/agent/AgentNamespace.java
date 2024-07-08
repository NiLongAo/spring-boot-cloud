package cn.com.tzy.springbootfs.config.socket.namespace.agent;

import cn.com.tzy.springbootcomm.common.jwt.JwtCommon;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.notify.AgentLoginSubscribe;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
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
        client.joinRoom(getSocketAgentKey(agent.getAgentKey()));
        RedisService.getAgentInfoManager().putAgentKey(client.getSessionId().toString(),agent.getAgentKey());
        String key = String.format("%s%s", AgentLoginSubscribe.FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER, agent.getAgentKey());
        Boolean lock = RedisUtils.getLock(key, 8L);
        if(lock){
            RedisUtils.releaseLock(key);
            agentService.login(agent.getAgentKey(),(data)->{
                client.sendEvent(AgentCommon.AGENT_OUT_LOGIN, RestResult.result(data.getCode(),data.getMessage(),data.getData()));
            });
        }else {
            //添加订阅
            RedisService.getAgentLoginSubscribe().addLoginSubscribe(agent.getAgentKey(),()->{
                RedisUtils.releaseLock(key);
                agentService.login(agent.getAgentKey(),(data)->{
                    client.sendEvent(AgentCommon.AGENT_OUT_LOGIN, RestResult.result(data.getCode(),data.getMessage(),data.getData()));
                });
            });
        }
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
        client.leaveRoom(getSocketAgentKey(agent.getAgentKey()));
        RedisService.getAgentInfoManager().delAgentKey(client.getSessionId().toString());
        String key = String.format("%s%s", AgentLoginSubscribe.FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER, agent.getAgentKey());
        //退出前加锁,退出成功后解锁
        RedisUtils.getLock(key,8L);
        agentService.logout(agent.getAgentKey(),(data)->{
            //成功后删除锁，并看是否有登录方法，如果有则执行登录 保证退出后才能登录
            RedisUtils.releaseLock(key);
            RedisUtils.redisTemplate.convertAndSend(key, agent.getAgentKey());//如果有登录执行
            log.info("退出消息：{}",data.getMessage());
        });
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

    public String getSocketAgentKey(String agentKey){
        return String.format("%s:%s", AgentCommon.SOCKET_AGENT,agentKey);
    }
}
