package cn.com.tzy.springbootfs.config.socket.event.agent;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.ValidatorUtils;
import cn.com.tzy.springbootfs.config.socket.common.agent.AgentInPhoneNotificationData;
import cn.com.tzy.springbootfs.config.socket.namespace.agent.AgentNamespace;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.AgentSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartersocketio.pool.EventListener;
import cn.com.tzy.springbootstartersocketio.pool.NamespaceListener;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * 接收事件回调
 * 判断用户是否接电话，是否继续通话等回调
 */
@Log4j2
@Component
public class AgentInPhoneNotificationEvent implements EventListener<AgentInPhoneNotificationData> {

    private final AgentNamespace agentNamespace;
    @Resource
    private SipServer sipServer;
    @Resource
    private AgentService agentService;

    public AgentInPhoneNotificationEvent(AgentNamespace agentNamespace) {
        this.agentNamespace = agentNamespace;
    }
    @Override
    public Class<AgentInPhoneNotificationData> getEventClass() {
        return AgentInPhoneNotificationData.class;
    }

    @Override
    public String getEventName() {
        return AgentCommon.AGENT_IN_CALL_NOTIFICATION;
    }

    @Override
    public NamespaceListener getNamespace() {
        return agentNamespace;
    }

    @Override
    public void onData(SocketIOClient client, AgentInPhoneNotificationData data, AckRequest ackSender) {
        //判断参数是否合法
        Set<ConstraintViolation<AgentInPhoneNotificationData>> validateFast = ValidatorUtils.validateFast(data);
        if(!validateFast.isEmpty()){
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_NOTIFICATION, RestResult.result(RespCode.CODE_2.getValue(),validateFast.stream().iterator().next().getMessage()));
            return;
        }
        //主叫
        String agentKey = RedisService.getAgentInfoManager().getAgentKey(client.getSessionId().toString());
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if (agentVoInfo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_NOTIFICATION, RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,未登录",agentKey)));
            return;
        }else if(data.getType() == null || (data.getType() !=1 && data.getType() !=2)){
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_NOTIFICATION, RestResult.result(RespCode.CODE_2.getValue(),String.format("客服 ： %s ,操作类型错误",agentKey)));
            return;
        }
        //1.判断用户是否接听 不接听则挂断电话 接听则继续
        if(data.getType() == 1){//挂断电话
            String key = String.format("%s%s", AgentSubscribeHandle.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, data.getCallId());
            RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]挂断电话操作",agentKey),null)));
            return;
        }
        //2.事件内部判断是否已推送流推送不成功则重新推送，开始准备推流相关业务操作
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentVoInfo);
        if (mediaServerVo == null) {
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_NOTIFICATION, RestResult.result(RespCode.CODE_2.getValue(),"未获取到可用流媒体"));
            String key = String.format("%s%s", AgentSubscribeHandle.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, data.getCallId());
            RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]挂断电话操作：无可用流媒体",agentKey),null)));
            return;
        }
        //拨打电话
        agentService.callPhone(data.getOnVideo() == ConstEnum.Flag.YES.getValue() ? VideoStreamType.CALL_VIDEO_PHONE :VideoStreamType.CALL_AUDIO_PHONE,sipServer,mediaServerVo,agentVoInfo,null,null,data.getCallId(),(code, msg, vo)->{
            client.sendEvent(AgentCommon.AGENT_OUT_CALL_PHONE,RestResult.result(code,msg,vo));
            if(code != RespCode.CODE_0.getValue()){
                //触发 INVITE 请求回调，开始继续下步流程
                String key = String.format("%s%s", AgentSubscribeHandle.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, data.getCallId());
                RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]接听电话操作",agentVoInfo.getAgentKey()),null)));
            }
        });
    }
}
