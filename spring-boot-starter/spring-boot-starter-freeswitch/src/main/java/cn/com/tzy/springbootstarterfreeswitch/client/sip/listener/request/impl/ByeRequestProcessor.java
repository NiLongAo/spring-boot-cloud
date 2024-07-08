package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

@Log4j2
@Component
public class ByeRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {


    @Override
    public String getMethod() {return Request.BYE;}

    @Override
    public void process(RequestEvent evt) {
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[回复BYE信息失败]，{}", e.getMessage());
        }
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
        String agentSip = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();
        SendRtp sendRtpItem =  sendRtpManager.querySendRTPServer(null, null, callIdHeader.getCallId());

        log.info("[收到bye] {}", agentSip);
        if (sendRtpItem != null){
            FsService.getSendAgentMessage().sendMessage(AgentCommon.SOCKET_AGENT,AgentCommon.AGENT_OUT_HANG_UP_PHONE,sendRtpItem.getAgentKey(), RestResult.result(RespCode.CODE_0.getValue(),"对方挂机"));//发送挂机命令
            log.info("[收到bye] 停止向上级推流：{}", sendRtpItem.getPushStreamId());
            MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtpItem.getMediaServerId());
            if(mediaServerVo != null){
                sendRtpManager.deleteSendRTPServer(sendRtpItem.getAgentKey(), null, callIdHeader.getCallId());
                if(sendRtpItem.getAudioInfo()!=null){
                    stopPush(sendRtpItem.getMediaServerId(),sendRtpItem.getAudioInfo());
                }
                if(sendRtpItem.getVideoInfo()!=null){
                    stopPush(sendRtpItem.getMediaServerId(),sendRtpItem.getVideoInfo());
                }
            }
        }
    }

    private void stopPush(String mediaServerId,SendRtp.SendRtpInfo sendRtpItem){
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        ssrcConfigManager.releaseSsrc(mediaServerId,sendRtpItem.getSsrc());
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(mediaServerId);
        if (mediaServerVo == null) {
            log.error("[停止RTP推流] 失败: 流媒体[{}]未上线",mediaServerId);
            return;
        }
        //关闭推流
        ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),sendRtpItem.getSsrc());
        //推出去的流也断开
        MediaClient.stopSendRtp(mediaServerVo,"__defaultVhost__",sendRtpItem.getApp(),sendRtpItem.getStreamId(),sendRtpItem.getSsrc());
        MediaClient.closeStreams(mediaServerVo,"__defaultVhost__",sendRtpItem.getApp(),sendRtpItem.getStreamId());
    }
}
