package cn.com.tzy.springbootfs.config.socket.event.agent;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfs.config.socket.common.agent.AgentInAnswerPhoneData;
import cn.com.tzy.springbootfs.config.socket.namespace.agent.AgentNamespace;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.AgentSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;

/**
 * 客服来电操作事件
 */
@Log4j2
@Component
public class AgentInAnswerPhoneEvent implements EventListener<AgentInAnswerPhoneData> {

    private final AgentNamespace agentNamespace;
    @Resource
    private SipServer sipServer;
    @Resource
    private AgentService agentService;
    public AgentInAnswerPhoneEvent(AgentNamespace agentNamespace) {
        this.agentNamespace = agentNamespace;
    }
    @Override
    public Class<AgentInAnswerPhoneData> getEventClass() {
        return AgentInAnswerPhoneData.class;
    }
    @Override
    public String getEventName() {
        return AgentCommon.AGENT_IN_ANSWER_PHONE;
    }
    @Override
    public NamespaceListener getNamespace() {
        return agentNamespace;
    }

    @Override
    public void onData(SocketIOClient client, AgentInAnswerPhoneData data, AckRequest request){
        //主叫
        String agentKey = RedisService.getAgentInfoManager().getAgentKey(client.getSessionId().toString());
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if (agentVoInfo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_ANSWER_PHONE,RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,未登录",agentKey)));
            return;
        }
        if(data.getType() == 1){//挂断电话
            String key = String.format("%s:%s", AgentSubscribeHandle.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, data.getCallId());
            RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new EventResult(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]挂断电话操作",agentKey),null))));
        }else if(data.getType() == 2){//接听电话
            String key = String.format("%s:%s", AgentSubscribeHandle.VIDEO_AGENT_OK_EVENT_SUBSCRIBE_MANAGER, data.getCallId());
            RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new EventResult(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]接听电话操作",agentKey),null))));
        }else {
            client.sendEvent(AgentCommon.AGENT_OUT_ANSWER_PHONE,RestResult.result(RespCode.CODE_2.getValue(),"当前操作类型不支持"));
            return;
        }
    }
}
