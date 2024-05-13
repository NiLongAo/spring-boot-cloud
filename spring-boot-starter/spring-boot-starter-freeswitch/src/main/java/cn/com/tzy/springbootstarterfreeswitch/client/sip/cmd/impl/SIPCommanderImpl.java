package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPRequestProvider;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.SipConfigProperties;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.TransportType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipMessageHandle;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SipTransactionInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SsrcTransaction;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.message.Request;
import java.text.ParseException;

/**
 * @description:设备能力接口
 */

@Log4j2
@Component
public class SIPCommanderImpl implements SIPCommander {

    @Resource
    private SipMessageHandle sipMessageHandle;

    @Override
    public void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, String stream, String callId, VideoStreamType type, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException, SsrcTransactionNotFoundException {
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        MediaServerVoService mediaServerService = SipService.getMediaServerService();
        SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentCode(), callId, stream,type);
        if(ssrcTransaction == null){
            log.info("[视频流停止]未找到视频流信息，设备：{}, 流ID: {}", agentVoInfo.getDeviceId(), stream);
            if(errorEvent != null){
                errorEvent.response(new EventResult<RestResultEvent>(new RestResultEvent(RespCode.CODE_2.getValue(),"未找到视频流信息")));
            }
            return;
        }
        MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(ssrcTransaction.getMediaServerId());
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(),ssrcTransaction.getSsrc());
        ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcTransaction.getStream());
        SipTransactionInfo sipTransactionInfo = ssrcTransaction.getSipTransactionInfo();
        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
        //构建器
        Request request = SIPRequestProvider.builder(sipServer, null, Request.BYE,null)
                .createSipURI(ssrcTransaction.getAgentCode(), agentVoInfo.getRemoteAddress())
                .addViaHeader(localIp, sipConfigProperties.getPort(),TransportType.UDP.getName(), false)
                .createFromHeader(agentVoInfo.getAgentCode(), sipConfigProperties.getIp(), sipTransactionInfo.getFromTag())
                .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getRemoteAddress(), sipTransactionInfo.getToTag())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createCallIdHeader(null,null,sipTransactionInfo.getCallId())
                .createUserAgentHeader()
                .createContactHeader(sipConfigProperties.getId(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .buildRequest();
        SipSendMessage.sendMessage(sipServer, agentVoInfo, request, ok->{
            if(mediaServerVo != null){
                MediaClient.closeRtpServer(mediaServerVo,ssrcTransaction.getStream());
                MediaClient.closeStreams(mediaServerVo,"__defaultVhost__",ssrcTransaction.getApp(),ssrcTransaction.getStream());
            }
            if(okEvent!= null){
                okEvent.response(ok);
            }
        },error->{
            if(mediaServerVo != null){
                MediaClient.closeRtpServer(mediaServerVo,ssrcTransaction.getStream());
            }
            if(errorEvent!= null){
                errorEvent.response(error);
            }
        });
    }

    @Override
    public void sendAckMessage(SipServer sipServer, SessionDescription sdp, ResponseEventExt event, SIPResponse response, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException, SdpParseException {
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
        Request reqAck = SIPRequestProvider.builder(sipServer, null, Request.ACK, null)
                .createSipURI(sdp.getOrigin().getUsername(), event.getRemoteIpAddress() + ":" + event.getRemotePort())
                .addViaHeader(response.getLocalAddress().getHostAddress(), sipConfigProperties.getPort(), response.getTopmostViaHeader().getTransport(), false)
                .createCallIdHeader(response.getCallIdHeader())
                .createFromHeader(response.getFromHeader())
                .createToHeader(response.getToHeader())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createContactHeader(sipConfigProperties.getId(),String.format("%s:%s",response.getLocalAddress().getHostAddress(), sipConfigProperties.getPort()))
                .createUserAgentHeader()
                .buildRequest();
        log.info("[回复ack] {}-> {}:{} ", sdp.getOrigin().getUsername(), event.getRemoteIpAddress(), event.getRemotePort());
        SipSendMessage.handleEvent(sipServer,response.getCallIdHeader().getCallId(),okEvent,errorEvent);
        sipMessageHandle.handleMessage(response.getLocalAddress().getHostAddress(),reqAck);
    }
}
