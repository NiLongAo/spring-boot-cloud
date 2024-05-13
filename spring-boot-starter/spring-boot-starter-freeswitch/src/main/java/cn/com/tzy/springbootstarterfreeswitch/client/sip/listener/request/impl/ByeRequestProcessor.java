package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedResult;
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
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();

        CallIdHeader callIdHeader = (CallIdHeader)evt.getRequest().getHeader(CallIdHeader.NAME);
        String channelId = ((SipURI) ((HeaderAddress) evt.getRequest().getHeader(ToHeader.NAME)).getAddress().getURI()).getUser();

        SendRtp sendRtpItem =  sendRtpManager.querySendRTPServer(channelId, null, callIdHeader.getCallId());
        log.info("[收到bye] {}", channelId);
        if (sendRtpItem != null){
            log.info("[收到bye] 停止向上级推流：{}", sendRtpItem.getStreamId());
            MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtpItem.getMediaServerId());
            sendRtpManager.deleteSendRTPServer( channelId, null, callIdHeader.getCallId());
            //关闭推流
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),sendRtpItem.getSsrc());
            MediaClient.stopSendRtp(mediaServerVo,"__defaultVhost__",sendRtpItem.getApp(),sendRtpItem.getStreamId(),sendRtpItem.getSsrc());
            OnStreamChangedResult result = MediaClient.getMediaInfo(mediaServerVo,"__defaultVhost__", "rtsp", sendRtpItem.getApp(), sendRtpItem.getStreamId());
            int totalReaderCount = 0;
            if(result != null && result.getCode() == RespCode.CODE_0.getValue()){
                totalReaderCount =result.getTotalReaderCount();
            }

        }
    }
}
