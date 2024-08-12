package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.AgentSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MessageTypeVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
        SipSendMessage.sendMessage(sipServer,vo,null,message,null,okEvent,errorEvent);
    }
    public static void sendMessage(SipServer sipServer, AgentVoInfo vo, String callId, SipSubscribeEvent handleEvent,SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        SipSendMessage.sendMessage(sipServer,vo,callId,null,handleEvent,okEvent,errorEvent);
    }
    private static void sendMessage(SipServer sipServer, AgentVoInfo vo, String callId,Message message,SipSubscribeEvent handleEvent, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        if(StringUtils.isEmpty(callId) && message != null){
            callId =((CallIdHeader) message.getHeader(CallIdHeader.NAME)).getCallId();
        }
        if(StringUtils.isEmpty(callId)){
            log.error("发送消息报错：callId is null");
            errorEvent.response(new EventResult(new RestResultEvent(RespCode.CODE_2.getValue(),"发送消息报错：callId is null",null)));
            return;
        }
        handleSipEvent(sipServer,callId,okEvent,errorEvent);
        if(message != null){
            LoginTypeEnum loginType = LoginTypeEnum.getLoginType(vo.getLoginType());
            RedisUtils.redisTemplate.convertAndSend(SipConstant.VIDEO_SEND_SIP_MESSAGE, Objects.requireNonNull(SerializationUtils.serialize(MessageTypeVo.builder().type(loginType == LoginTypeEnum.SIP?MessageTypeVo.TypeEnum.SIP.getValue():MessageTypeVo.TypeEnum.SOCKET.getValue()).agentKey(vo.getAgentKey()).message(message).build())));
        }
        if(handleEvent != null){
            handleEvent.response(new EventResult(new RestResultEvent(RespCode.CODE_0.getValue(),"发送消息完成",null)));
        }
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

