package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPRequestProvider;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.SipConfigProperties;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.CharsetType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.StreamModeType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.TransportType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.PlatformRegisterManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SipTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.ParentPlatformService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Arrays;

@Log4j2
@Component
public class SIPCommanderFroPlatformImpl implements SIPCommanderForPlatform {

    @Resource
    private MediaHookSubscribe mediaHookSubscribe;

    @Override
    public void unregister(SipServer sipServer, AgentVoInfo agentVoInfo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, ParseException, SipException {
        register(sipServer, agentVoInfo, null, false,okEvent,errorEvent);
    }

    @Override
    public void register(SipServer sipServer, AgentVoInfo agentVoInfo, @Nullable WWWAuthenticateHeader www, boolean isRegister, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException {
        SipTransactionManager sipTransactionManager = RedisService.getSipTransactionManager();
        ParentPlatformService parentPlatformService = SipService.getParentPlatformService();
        SipTransactionInfo sipTransactionInfo = sipTransactionManager.findParentPlatform(agentVoInfo.getAgentCode());
        ConfigModel configModel = parentPlatformService.random();
        SipConfigProperties deviceSipConfig = sipServer.getSipConfigProperties();
        agentVoInfo.setFsHost(configModel.getRemoteIp());
        agentVoInfo.setFsPost(configModel.getInternalPort());
        SIPRequest request;
        String callId = null;
        String fromTag = SipUtils.getNewFromTag();
        String toTag = null;
        if (sipTransactionInfo != null ) {
            if (StringUtils.isNotEmpty(sipTransactionInfo.getCallId())) {
                callId = sipTransactionInfo.getCallId();
            }
            if (StringUtils.isNotEmpty(sipTransactionInfo.getFromTag())) {
                fromTag = sipTransactionInfo.getFromTag();
            }
            if (StringUtils.isNotEmpty(sipTransactionInfo.getToTag())) {
                toTag = sipTransactionInfo.getToTag();
            }
        }
        PlatformRegisterManager platformRegisterManager = RedisService.getPlatformRegisterManager();
        if (www == null) {
            request =(SIPRequest) SIPRequestProvider.builder(sipServer, null, Request.REGISTER, null)
                    .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                    .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), callId)
                    .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), fromTag)
                    .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), null)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createUserAgentHeader()
                    .createContactHeader(agentVoInfo.getAgentCode(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                    .createExpiresHeader(isRegister ? agentVoInfo.getExpires() : 0)
                    .buildRequest();
            CallIdHeader callIdHeader = request.getCallIdHeader();
            //存储注册缓存
            platformRegisterManager.updatePlatformRegisterInfo(callIdHeader.getCallId(), PlatformRegisterInfo.builder().agentCode(agentVoInfo.getAgentCode()).register(isRegister).build());
        }else {
            request = (SIPRequest) SIPRequestProvider.builder(sipServer, null, Request.REGISTER, null)
                    .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                    .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), callId)
                    .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), fromTag)
                    .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), null)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createUserAgentHeader()
                    .createContactHeader(agentVoInfo.getAgentCode(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                    .createExpiresHeader(isRegister ? agentVoInfo.getExpires() : 0)
                    .createAuthorizationHeader(agentVoInfo.getAgentCode(), agentVoInfo.getAgentCode(),String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()), agentVoInfo.getPasswd(),www)
                    .buildRequest();
        }
        SipSendMessage.sendMessage(sipServer, agentVoInfo, request,okEvent, error->{
            log.info("向上级平台 [ {} ] {}发生错误： {} ", agentVoInfo.getAgentCode(),isRegister?"注册":"注销",error.getMsg());
            if(isRegister){
                CallIdHeader callIdHeader = request.getCallIdHeader();
                platformRegisterManager.delPlatformRegisterInfo(callIdHeader.getCallId());
            }
            if(errorEvent != null){
                errorEvent.response(error);
            }
        });
        if(isRegister){
            RedisService.getAgentInfoManager().put(agentVoInfo);
        }
    }

    @Override
    public String keepalive(SipServer sipServer, AgentVoInfo agentVoInfo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException {
        SipConfigProperties deviceSipConfig = sipServer.getSipConfigProperties();
            String characterSet = CharsetType.GB2312.getName();
            StringBuffer keepaliveXml = new StringBuffer(200);
            keepaliveXml.append("<?xml version=\"1.0\" encoding=\"" + characterSet + "\"?>\r\n");
            keepaliveXml.append("<Notify>\r\n");
            keepaliveXml.append("<CmdType>Keepalive</CmdType>\r\n");
            keepaliveXml.append("<SN>" + (int)((Math.random()*9+1)*100000) + "</SN>\r\n");
            keepaliveXml.append("<DeviceID>" + agentVoInfo.getAgentCode() + "</DeviceID>\r\n");
            keepaliveXml.append("<Status>OK</Status>\r\n");
            keepaliveXml.append("</Notify>\r\n");
        Request request = SIPRequestProvider.builder(sipServer, null, Request.MESSAGE, keepaliveXml.toString())
                .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), null)
                .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), null)
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createUserAgentHeader()
                .createContentTypeHeader("application", "manscdp+xml")
                .buildRequest();
        SipSendMessage.sendMessage(sipServer, agentVoInfo, request,okEvent,errorEvent);
        CallIdHeader header = (CallIdHeader)request.getHeader(CallIdHeader.NAME);
        return header.getCallId();
    }

    @Override
    public void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, SendRtp sendRtpItem, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException {
        if (sendRtpItem == null ) {
            log.info("[向上级发送BYE]， sendRtpItem 为NULL");
            return;
        }
        if (agentVoInfo == null) {
            log.info("[向上级发送BYE]， agentInfo 为NULL");
            return;
        }
        log.info("[向上级发送BYE]， {}", agentVoInfo.getAgentCode());
        SipConfigProperties deviceSipConfig = sipServer.getSipConfigProperties();
        Request request = SIPRequestProvider.builder(sipServer, CharsetType.GB2312.getName(), Request.BYE, null)
                .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                .createCallIdHeader(null, null, sendRtpItem.getCallId())
                .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), null)
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createUserAgentHeader()
                .createContactHeader(agentVoInfo.getAgentCode(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                .buildRequest();
        if (request == null) {
            log.warn("[向上级发送bye]：无法创建 byeRequest");
            return;
        }
        SipSendMessage.sendMessage(sipServer, agentVoInfo,request,okEvent,errorEvent);
    }

    @Override
    public void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, String stream, String callId, VideoStreamType type, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException {

        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        MediaServerVoService mediaServerService = SipService.getMediaServerService();
        SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentCode(), callId, stream,type);
        if(ssrcTransaction == null){
            log.info("[视频流停止]未找到视频流信息，坐席：{}, 流ID: {}", agentVoInfo.getAgentCode(), stream);
            if(errorEvent != null){
                errorEvent.response(new EventResult(new RestResultEvent(RespCode.CODE_2.getValue(),"未找到视频流信息")));
            }
            return;
        }
        MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(ssrcTransaction.getMediaServerId());
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(),ssrcTransaction.getSsrc());
        ssrcTransactionManager.remove(ssrcTransaction.getAgentCode(),ssrcTransaction.getStream());
        SipTransactionInfo sipTransactionInfo = ssrcTransaction.getSipTransactionInfo();
        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
        //构建器
        Request request = SIPRequestProvider.builder(sipServer, null, Request.BYE,null)
                .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.getName(agentVoInfo.getTransport()), false)
                .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), sipTransactionInfo.getFromTag())
                .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), sipTransactionInfo.getToTag())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createCallIdHeader(null,null,sipTransactionInfo.getCallId())
                .createUserAgentHeader()
                .createContactHeader(sipConfigProperties.getId(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .buildRequest();
        SipSendMessage.sendMessage(sipServer,agentVoInfo, request,ok->{
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
    public SIPRequest presenceSubscribe(SipServer sipServer, AgentVoInfo agentVoInfo, SIPRequest requestOld, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException {

        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();;

        //构建器
        Request request = SIPRequestProvider.builder(sipServer, null, Request.SUBSCRIBE, null)
                .createSipURI(agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.UDP.getName(), true)
                .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), null)
                .createCallIdHeader(sipConfigProperties.getIp(), TransportType.UDP.getName(),requestOld == null?null:requestOld.getCallIdHeader().getCallId())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createUserAgentHeader()
                .createContactHeader(agentVoInfo.getAgentCode(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .createExpiresHeader(600)
                .createEventHeader(null,"presence")
                .createAcceptHeader("application","pidf+xml")
                .createContentTypeHeader("application", "pidf+xml")
                .buildRequest();
        SipSendMessage.sendMessage(sipServer,agentVoInfo, request,okEvent,errorEvent);
        return (SIPRequest)  request;
    }

    @Override
    public SIPRequest callPhone(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo ssrcInfo, AgentVoInfo agentVoInfo, String caller,HookEvent hookEvent, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException {
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        String stream = ssrcInfo.getStream();
        if (agentVoInfo == null) {
            return null;
        }
        //创建发流端口
        boolean tcp = Arrays.asList("TCP-PASSIVE","TCP-ACTIVE").contains(StreamModeType.getName(agentVoInfo.getStreamMode()));
        boolean tcpActive= false;
        if(tcp){
            tcpActive = "TCP-ACTIVE".equals(StreamModeType.getName(agentVoInfo.getStreamMode()));
        }
        SendRtp sendRtp = MediaClient.createSendRtp(mediaServerVo,null, null, 0,ssrcInfo.getSsrc(),agentVoInfo.getAgentCode(),"push_web_rtp",agentVoInfo.getAgentCode(), tcp,tcpActive,sipServer.getVideoProperties().getServerId(),null,true, null);
        if (sendRtp == null) {
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
            errorEvent.response(new EventResult(new RestResultEvent(RespCode.CODE_2.getValue(),"[视频语音流ACK] sendRtp is null 服务器端口资源不足")));
            return null;
        }
        log.info("{} 分配的ZLM为: {} [{}:{}]", stream, mediaServerVo.getId(), mediaServerVo.getSdpIp(), ssrcInfo.getPort());
        String sdpIp =mediaServerVo.getSdpIp();
        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append("o=- 0 0 IN IP4 " + sdpIp + "\r\n");
        content.append("s=pjmedia\r\n");
        content.append("b=AS:84\r\n");
        content.append("t=0 0\r\n");
        content.append("a=X-nat:0\r\n");
        if(tcpActive){
            content.append("m=audio " + ssrcInfo.getPort() + " TCP/RTP/AVP 8 0 101\r\n");
        }else if(tcp){
            content.append("m=audio " + ssrcInfo.getPort() + " TCP/RTP/AVP 8 0 101\r\n");
        }else {
            content.append("m=audio " + ssrcInfo.getPort() + " RTP/AVP 8 0 101\r\n");
        }
        content.append("c=IN IP4 " + sdpIp + "\r\n");
        content.append("b=TIAS:64000\r\n");
        content.append("a=rtcp:" + sendRtp.getLocalPort() + " IN IP4 " + sdpIp + "\r\n");
        content.append("a=sendrecv\r\n");//sendrecv 双向传输（发送和接收）  recvonly
        content.append("a=rtpmap:8 PCMA/8000\r\n");
        content.append("a=rtpmap:0 PCMU/8000\r\n");
        content.append("a=rtpmap:101 telephone-event/8000\r\n");
        content.append("a=fmtp:101 0-16\r\n");
        if (tcpActive) { // tcp主动模式
            content.append("a=setup:active\r\n");
            content.append("a=connection:new\r\n");
        }else if(tcp){
            content.append("a=setup:passive\r\n");
            content.append("a=connection:new\r\n");
        }
        content.append("a=ssrc:"+ssrcInfo.getSsrc() + "\r\n");//ssrc
        // f字段:f= v/编码格式/分辨率/帧率/码率类型/码率大小a/编码格式/码率大小/采样率
        // content.append("f=v/2/5/25/1/4000a/1/8/1" + "\r\n"); // 未发现支持此特性的设备
        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();;
        //构建器
        SIPRequest request = (SIPRequest)SIPRequestProvider.builder(sipServer, null, Request.INVITE, content.toString())
                .createSipURI(caller, String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.UDP.getName(), true)
                .createCallIdHeader(localIp,TransportType.getName(agentVoInfo.getTransport()),null)
                .createFromHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(caller, agentVoInfo.getFsHost(), null)
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createContentTypeHeader("application", "sdp")
                .createContactHeader(agentVoInfo.getAgentCode(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .createUserAgentHeader()
                .buildRequest();
        String callId = request.getCallId().getCallId();
        //添加流变动回调
        if(hookEvent != null){
            HookKey hookKey = HookKeyFactory.onStreamChanged("rtp", stream, true, "rtsp", mediaServerVo.getId());
            mediaHookSubscribe.addSubscribe(hookKey,(MediaServerVo mediaServer, HookVo response)->{
                response.setCallId(callId);
                hookEvent.response(mediaServer,response);
                mediaHookSubscribe.removeSubscribe(hookKey);
            });
        }
        sendRtp.setCallId(callId);
        RedisService.getSendRtpManager().put(sendRtp);
        SipSendMessage.sendMessage(sipServer,agentVoInfo, request,(ok)->{
            ResponseEvent event = (ResponseEvent) ok.getEvent();
            SIPResponse response = (SIPResponse) event.getResponse();
            // 这里为例避免一个通道的点播多次点播只有一个callID这个参数使用一个固定值
            ssrcTransactionManager.put(agentVoInfo.getAgentCode(),callId,"rtp",stream, ssrcInfo.getSsrc(), agentVoInfo.getAgentCode(),response, VideoStreamType.call_phone);
            okEvent.response(ok);
        },(error)->{
            RedisService.getSendRtpManager().deleteSendRTPServer(agentVoInfo.getAgentCode(),agentVoInfo.getAgentCode(),callId);
            ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcInfo.getStream(),callId,null);
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
            errorEvent.response(error);
        });
        return request;
    }
}
