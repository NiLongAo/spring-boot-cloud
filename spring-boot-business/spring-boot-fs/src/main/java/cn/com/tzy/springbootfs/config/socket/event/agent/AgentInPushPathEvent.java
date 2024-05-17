package cn.com.tzy.springbootfs.config.socket.event.agent;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootfs.config.socket.common.agent.AgentInPushPathData;
import cn.com.tzy.springbootfs.config.socket.namespace.agent.AgentNamespace;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 客服推流地址
 */
@Log4j2
@Component
public class AgentInPushPathEvent implements EventListener<AgentInPushPathData> {

    private final AgentNamespace agentNamespace;
    @Resource
    private AgentService agentService;
    public AgentInPushPathEvent(AgentNamespace agentNamespace) {
        this.agentNamespace = agentNamespace;
    }
    @Override
    public Class<AgentInPushPathData> getEventClass() {
        return AgentInPushPathData.class;
    }
    @Override
    public String getEventName() {
        return AgentCommon.AGENT_IN_PUSH_PATH;
    }
    @Override
    public NamespaceListener getNamespace() {
        return agentNamespace;
    }
    @Override
    public void onData(SocketIOClient client, AgentInPushPathData data, AckRequest request) throws IOException {
        //主叫
        String agentCode = RedisService.getAgentInfoManager().getAgentCode(client.getSessionId().toString());
        //获取推流地址
        RestResult<?> result = agentService.pushPath(agentCode, data.getType());
        client.sendEvent(AgentCommon.AGENT_OUT_PUSH_PATH,result);
    }
}
