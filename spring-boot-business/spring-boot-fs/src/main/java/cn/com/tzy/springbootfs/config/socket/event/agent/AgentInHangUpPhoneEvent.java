package cn.com.tzy.springbootfs.config.socket.event.agent;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfs.config.socket.common.agent.AgentInHangUpPhoneData;
import cn.com.tzy.springbootfs.config.socket.namespace.agent.AgentNamespace;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.InviteInfo;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * 客服更变自身状态 繁忙或者空闲 状态
 */
@Log4j2
@Component
public class AgentInHangUpPhoneEvent implements EventListener<AgentInHangUpPhoneData> {

    private final AgentNamespace agentNamespace;

    @Resource
    private SipServer sipServer;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    public AgentInHangUpPhoneEvent(AgentNamespace agentNamespace) {
        this.agentNamespace = agentNamespace;
    }
    @Override
    public Class<AgentInHangUpPhoneData> getEventClass() {
        return AgentInHangUpPhoneData.class;
    }
    @Override
    public String getEventName() {
        return AgentCommon.AGENT_IN_HANG_UP_PHONE;
    }
    @Override
    public NamespaceListener getNamespace() {
        return agentNamespace;
    }

    @Override
    public void onData(SocketIOClient client, AgentInHangUpPhoneData data, AckRequest request){
        //主叫
        String agentKey = RedisService.getAgentInfoManager().getAgentKey(client.getSessionId().toString());
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if (agentVoInfo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_HANG_UP_PHONE,RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,未登录",agentKey)));
            return;
        }
        InviteInfo inviteInfo = RedisService.getInviteStreamManager().getInviteInfoByDeviceAndChannel(null,agentVoInfo.getAgentKey());
        if(inviteInfo == null){
            client.sendEvent(AgentCommon.AGENT_OUT_HANG_UP_PHONE,RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,未获取拨打电话请求",agentKey)));
            return;
        }
        try {
            sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,inviteInfo.getAudioSsrcInfo().getStream(),inviteInfo.getVideoSsrcInfo()==null?null:inviteInfo.getVideoSsrcInfo().getStream(),null,null,(ok)->{
                client.sendEvent(AgentCommon.AGENT_OUT_HANG_UP_PHONE,RestResult.result(RespCode.CODE_0.getValue(),"挂机成功"));
            },(error)->{
                client.sendEvent(AgentCommon.AGENT_OUT_HANG_UP_PHONE,RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,BYE异常：%s",agentKey,error.getMsg())));
            });
        }catch (InvalidArgumentException | ParseException | SipException e){
            log.error("[无人观看]点播， 发送BYE失败 {}", e.getMessage());
            client.sendEvent(AgentCommon.AGENT_OUT_HANG_UP_PHONE,RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,发送BYE失败",agentKey)));
        }
    }
}
