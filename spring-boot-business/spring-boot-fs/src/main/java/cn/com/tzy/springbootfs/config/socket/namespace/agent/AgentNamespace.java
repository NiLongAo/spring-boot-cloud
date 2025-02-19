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
    private SocketIOServer socketIOServer;

    @Resource
    private AgentService agentService;

    @Override
    public String getNamespaceName() {
        return AgentCommon.SOCKET_AGENT;
    }

    @Override
    public void onConnect(SocketIOClient client) {
        log.info("客户端:{} 已连接", client.getSessionId());
        Agent agent = authenticateClient(client);
        if (agent == null) return;

        client.joinRoom(getSocketAgentKey(agent.getAgentKey()));
        RedisService.getAgentInfoManager().putAgentKey(client.getSessionId().toString(), agent.getAgentKey());
        handleAgentLogin(client, agent);
    }

    @Override
    public void onDisconnect(SocketIOClient client) {
        log.info("客户端:{} 断开连接", client.getSessionId());
        Agent agent = authenticateClient(client);
        if (agent == null) return;

        client.leaveRoom(getSocketAgentKey(agent.getAgentKey()));
        RedisService.getAgentInfoManager().delAgentKey(client.getSessionId().toString());
        handleAgentLogout(agent);
    }

    private Agent authenticateClient(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        List<String> authTokens = urlParams.get(JwtCommon.JWT_AUTHORIZATION_KEY);
        if (authTokens.isEmpty()) {
            log.error("客户端:{} 未获取到 PublicMemberNamespace 连接", client.getSessionId());
            return null;
        }
        Long userId = extractUserId(authTokens.get(0));
        Agent agent = agentService.findUserId(userId);
        if (agent == null) {
            log.error("客户端:{} 用户：{} 未获取到客服信息", client.getSessionId(), userId);
        }
        return agent;
    }

    private Long extractUserId(String token) {
        Map<String, String> claims = JwtUtils.builder(JwtCommon.JWT_AUTHORIZATION_KEY, token)
                .setPrefix(JwtCommon.AUTHORIZATION_PREFIX)
                .builderJwtUser(null);
        return MapUtil.getLong(claims, JwtCommon.JWT_USER_ID);
    }

    private void handleAgentLogin(SocketIOClient client, Agent agent) {
        String key = String.format("%s%s", AgentLoginSubscribe.FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER, agent.getAgentKey());
        if (RedisUtils.getLock(key, 8L)) {
            agentService.login(agent.getAgentKey(), data -> {
                client.sendEvent(AgentCommon.AGENT_OUT_LOGIN, RestResult.result(data.getCode(), data.getMessage(), data.getData()));
                RedisUtils.releaseLock(key);
            });
        } else {
            log.warn("已被加锁，订阅延迟登陆");
            RedisService.getAgentLoginSubscribe().addLoginSubscribe(agent.getAgentKey(), () -> {
                agentService.login(agent.getAgentKey(), data -> {
                    client.sendEvent(AgentCommon.AGENT_OUT_LOGIN, RestResult.result(data.getCode(), data.getMessage(), data.getData()));
                    RedisUtils.releaseLock(key);
                });
            });
        }
    }

    private void handleAgentLogout(Agent agent) {
        String key = String.format("%s%s", AgentLoginSubscribe.FS_AGENT_LOGIN_EVENT_SUBSCRIBE_MANAGER, agent.getAgentKey());
        RedisUtils.getLock(key, 8L);
        agentService.logout(agent.getAgentKey(), data -> {
            RedisUtils.releaseLock(key);
            log.warn("退出完成，执行登陆操作");
            RedisUtils.redisTemplate.convertAndSend(key, agent.getAgentKey());
            log.info("退出消息：{}", data.getMessage());
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

    public Namespace getNamespace() {
        return (Namespace) socketIOServer.getNamespace(AgentCommon.SOCKET_AGENT);
    }

    public String getSocketAgentKey(String agentKey) {
        return String.format("%s:%s", AgentCommon.SOCKET_AGENT, agentKey);
    }
}