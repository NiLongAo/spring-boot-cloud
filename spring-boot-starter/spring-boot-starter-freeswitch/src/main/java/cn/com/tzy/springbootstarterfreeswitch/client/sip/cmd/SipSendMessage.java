package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.AgentSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MessageTypeVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.SerializationUtils;

import javax.sip.header.CallIdHeader;
import javax.sip.message.Message;
import java.util.Objects;

/**
 * 消息发送类
 */
@Log4j2
public class SipSendMessage {

    /**
     * 处理上级发送消息事件
     */
    public static void sendMessage(SipServer sipServer, AgentVoInfo vo, Message message, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        CallIdHeader callIdHeader = (CallIdHeader) message.getHeader(CallIdHeader.NAME);
        handleSipEvent(sipServer,callIdHeader.getCallId(),okEvent,errorEvent);
        LoginTypeEnum loginType = LoginTypeEnum.getLoginType(vo.getLoginType());
        RedisUtils.redisTemplate.convertAndSend(SipConstant.VIDEO_SEND_SIP_MESSAGE, Objects.requireNonNull(SerializationUtils.serialize(MessageTypeVo.builder().type(loginType == LoginTypeEnum.SIP?MessageTypeVo.TypeEnum.SIP.getValue():MessageTypeVo.TypeEnum.SOCKET.getValue()).agentKey(vo.getAgentKey()).message(message).build())));
    }

    public static void handleSipEvent(SipServer sipServer, String callId, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        SipSubscribeHandle sipSubscribeHandle = sipServer.getSipSubscribeManager();
        // 添加成功订阅
        if (okEvent != null) {
            sipSubscribeHandle.addOkSubscribe(callId, eventResult -> {
                okEvent.response(eventResult);
                sipSubscribeHandle.removeAllSubscribe(eventResult.getCallId());
            });
        }
        // 添加错误订阅
        if (errorEvent != null) {
            sipSubscribeHandle.addErrorSubscribe(callId, (eventResult -> {
                errorEvent.response(eventResult);
                sipSubscribeHandle.removeAllSubscribe(eventResult.getCallId());
            }));
        }
    }

    public static void handleAgentEvent(SipServer sipServer, String callId, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        AgentSubscribeHandle agentSubscribeManager = sipServer.getAgentSubscribeManager();
        // 添加成功订阅
        if (okEvent != null) {
            agentSubscribeManager.addOkSubscribe(callId, eventResult -> {
                okEvent.response(eventResult);
                agentSubscribeManager.removeAllSubscribe(eventResult.getCallId());
            });
        }
        // 添加错误订阅
        if (errorEvent != null) {
            agentSubscribeManager.addErrorSubscribe(callId, (eventResult -> {
                errorEvent.response(eventResult);
                agentSubscribeManager.removeAllSubscribe(eventResult.getCallId());
            }));
        }
    }
}

