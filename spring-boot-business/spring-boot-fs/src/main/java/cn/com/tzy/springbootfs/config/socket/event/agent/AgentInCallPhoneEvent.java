package cn.com.tzy.springbootfs.config.socket.event.agent;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootfs.config.socket.common.agent.AgentInCallPhoneData;
import cn.com.tzy.springbootfs.config.socket.namespace.agent.AgentNamespace;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 客服拨打电话
 */
@Log4j2
@Component
public class AgentInCallPhoneEvent implements EventListener<AgentInCallPhoneData> {

    private final AgentNamespace agentNamespace;
    @Resource
    private SipServer sipServer;
    @Resource
    private AgentService agentService;
    public AgentInCallPhoneEvent(AgentNamespace agentNamespace) {
        this.agentNamespace = agentNamespace;
    }
    @Override
    public Class<AgentInCallPhoneData> getEventClass() {
        return AgentInCallPhoneData.class;
    }
    @Override
    public String getEventName() {
        return AgentCommon.AGENT_IN_CALL_PHONE;
    }
    @Override
    public NamespaceListener getNamespace() {
        return agentNamespace;
    }

    @Override
    public void onData(SocketIOClient client, AgentInCallPhoneData data, AckRequest request){
        //主叫
        String agentKey = RedisService.getAgentInfoManager().getAgentKey(client.getSessionId().toString());
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentVoInfo);
        if (mediaServerVo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_PHONE,RestResult.result(RespCode.CODE_0.getValue(),"未获取到可用流媒体"));
            return;
        } else if (agentVoInfo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_PHONE,RestResult.result(RespCode.CODE_0.getValue(),String.format("客服 ： %s ,未登录",agentKey)));
            return;
        }
        //被叫
        String caller = data.getCaller();
        //拨打电话
        agentService.callPhone(data.getType(),sipServer,mediaServerVo,agentVoInfo,caller,null,(code,msg,vo)->{
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_PHONE,RestResult.result(code,msg,vo));
        });
    }
}
