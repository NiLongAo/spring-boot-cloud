package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPRequestProvider;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.SipConfigProperties;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
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
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.AgentSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.ParentPlatformService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.util.RandomUtil;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ProxyAuthenticateHeader;
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
        SipTransactionInfo sipTransactionInfo = sipTransactionManager.findParentPlatform(agentVoInfo.getAgentKey());
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
                    .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                    .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), callId)
                    .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), fromTag)
                    .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), null)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createUserAgentHeader()
                    .createContactHeader(agentVoInfo.getCalled(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                    .createExpiresHeader(isRegister ? agentVoInfo.getExpires() : 0)
                    .buildRequest();
            CallIdHeader callIdHeader = request.getCallIdHeader();
            //存储注册缓存
            platformRegisterManager.updatePlatformRegisterInfo(callIdHeader.getCallId(), PlatformRegisterInfo.builder().agentKey(agentVoInfo.getAgentKey()).register(isRegister).build());
        }else {
            request = (SIPRequest) SIPRequestProvider.builder(sipServer, null, Request.REGISTER, null)
                    .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                    .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), callId)
                    .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), fromTag)
                    .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), null)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createUserAgentHeader()
                    .createContactHeader(agentVoInfo.getCalled(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                    .createExpiresHeader(isRegister ? agentVoInfo.getExpires() : 0)
                    .createAuthorizationHeader(Request.REGISTER,agentVoInfo.getCalled(), agentVoInfo.getCalled(),String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()), agentVoInfo.getPasswd(),www)
                    .buildRequest();
        }
        SipSendMessage.sendMessage(sipServer, agentVoInfo, request,okEvent, error->{
            log.info("向上级平台 [ {} ] {}发生错误： {} ", agentVoInfo.getAgentKey(),isRegister?"注册":"注销",error.getMsg());
            if(isRegister){
                CallIdHeader callIdHeader = request.getCallIdHeader();
                platformRegisterManager.delPlatformRegisterInfo(callIdHeader.getCallId());
            }
            if(errorEvent != null){
                errorEvent.response(error);
            }
        });
        if(isRegister){
            if(agentVoInfo.getAgentState() == null){
                agentVoInfo.setAgentState(AgentStateEnum.LOGIN);
            }
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
            keepaliveXml.append("<DeviceID>" + agentVoInfo.getCalled() + "</DeviceID>\r\n");
            keepaliveXml.append("<Status>OK</Status>\r\n");
            keepaliveXml.append("</Notify>\r\n");
        Request request = SIPRequestProvider.builder(sipServer, null, Request.MESSAGE, keepaliveXml.toString())
                .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                .createCallIdHeader(deviceSipConfig.getIp(), TransportType.UDP.getName(), null)
                .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), null)
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
        log.info("[向上级发送BYE]， {}", agentVoInfo.getAgentKey());
        SipConfigProperties deviceSipConfig = sipServer.getSipConfigProperties();
        Request request = SIPRequestProvider.builder(sipServer, CharsetType.GB2312.getName(), Request.BYE, null)
                .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(deviceSipConfig.getIp(), deviceSipConfig.getPort(), TransportType.UDP.getName(), true)
                .createCallIdHeader(null, null, sendRtpItem.getCallId())
                .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), null)
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createUserAgentHeader()
                .createContactHeader(agentVoInfo.getCalled(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                .buildRequest();
        if (request == null) {
            log.warn("[向上级发送bye]：无法创建 byeRequest");
            return;
        }
        RedisService.getSendRtpManager().deleteSendRTPServer(sendRtpItem.getAgentKey(),sendRtpItem.getPushStreamId(),sendRtpItem.getCallId());//挂断时，删除sendRtp
        SipSendMessage.sendMessage(sipServer, agentVoInfo,request,okEvent,errorEvent);
    }

    @Override
    public void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, String audioStream, String videoStream, String callId, VideoStreamType type, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException {
        SsrcTransaction ssrcTransaction = this.streamByeCmd(agentVoInfo,audioStream,callId,type,errorEvent);
        if(StringUtils.isNotEmpty(videoStream)){
            ssrcTransaction = streamByeCmd(agentVoInfo,videoStream,callId,type,errorEvent);
        }
        if(ssrcTransaction == null){
            return;
        }
        RedisService.getSendRtpManager().deleteSendRTPServer(ssrcTransaction.getAgentKey(),null,ssrcTransaction.getCallId());//挂断时，删除sendRtp
        SipTransactionInfo sipTransactionInfo = ssrcTransaction.getSipTransactionInfo();
        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
        //构建器
        Request request = SIPRequestProvider.builder(sipServer, CharsetType.GB2312.getName(), Request.BYE,null)
                .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.getName(agentVoInfo.getTransport()), false)
                .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), sipTransactionInfo.getFromTag())
                .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), sipTransactionInfo.getToTag())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createCallIdHeader(null,null,sipTransactionInfo.getCallId())
                .createUserAgentHeader()
                .createContactHeader(sipConfigProperties.getId(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .buildRequest();
        SipSendMessage.sendMessage(sipServer,agentVoInfo, request,ok->{
            if(okEvent!= null){
                okEvent.response(ok);
            }
        },error->{
            if(errorEvent!= null){
                errorEvent.response(error);
            }
        });
    }

    private SsrcTransaction streamByeCmd(AgentVoInfo agentVoInfo, String stream, String callId, VideoStreamType type, SipSubscribeEvent errorEvent){
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        MediaServerVoService mediaServerService = SipService.getMediaServerService();
        SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentKey(), callId, stream,type);
        if(ssrcTransaction == null){
            log.info("[视频流停止]未找到视频流信息，坐席：{}, 流 callId: {}, 流ID: {}", agentVoInfo.getAgentKey(),callId, stream);
            if(errorEvent != null){
                errorEvent.response(new EventResult(new RestResultEvent(RespCode.CODE_2.getValue(),"未找到视频流信息")));
            }
            return null;
        }
        SipTransactionInfo sipTransactionInfo = ssrcTransaction.getSipTransactionInfo();
        if(sipTransactionInfo == null){
            log.info("[视频流停止]当前流未请求成功，无法关闭，设备：{}, 流ID: {}", agentVoInfo.getDeviceId(), stream);
            if(errorEvent != null){
                errorEvent.response(new EventResult<RestResultEvent>(new RestResultEvent(RespCode.CODE_2.getValue(),"当前流未请求成功，无法关闭")));
            }
            return null;
        }
        MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(ssrcTransaction.getMediaServerId());
        if(mediaServerVo != null){
            //MediaClient.closeRtpServer(mediaServerVo,ssrcTransaction.getStream());
            MediaClient.closeStreams(mediaServerVo,"__defaultVhost__",ssrcTransaction.getApp(),ssrcTransaction.getStream());
        }
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(),ssrcTransaction.getSsrc());
        ssrcTransactionManager.remove(ssrcTransaction.getAgentKey(),ssrcTransaction.getStream());
        return ssrcTransaction;
    }

    @Override
    public SIPRequest presenceSubscribe(SipServer sipServer, AgentVoInfo agentVoInfo, SIPRequest requestOld, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException {

        String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
        SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();;

        //构建器
        Request request = SIPRequestProvider.builder(sipServer, null, Request.SUBSCRIBE, null)
                .createSipURI(agentVoInfo.getCalled(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.UDP.getName(), true)
                .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                .createToHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), null)
                .createCallIdHeader(sipConfigProperties.getIp(), TransportType.UDP.getName(),requestOld == null?null:requestOld.getCallIdHeader().getCallId())
                .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                .createUserAgentHeader()
                .createContactHeader(agentVoInfo.getCalled(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                .createExpiresHeader(600)
                .createEventHeader(null,"presence")
                .createAcceptHeader("application","pidf+xml")
                .createContentTypeHeader("application", "pidf+xml")
                .buildRequest();
        SipSendMessage.sendMessage(sipServer,agentVoInfo, request,okEvent,errorEvent);
        return (SIPRequest)  request;
    }

    @Override
    public SIPRequest callPhone(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo agentVoInfo, String caller,String callBackId,HookEvent hookEvent, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException {
        return callPhone(sipServer,mediaServerVo,videoSsrcInfo,audioSsrcInfo,agentVoInfo,caller,null,null,null,callBackId,hookEvent,okEvent,errorEvent);
    }

    @Override
    public SIPRequest callPhone(SipServer sipServer,AgentVoInfo agentVoInfo,ProxyAuthenticateHeader header,SIPRequest sipRequest,SIPResponse response) throws InvalidArgumentException, SipException, ParseException {
        return callPhone(sipServer,null,null,null,agentVoInfo,null,header,sipRequest,response,null,null,null,null);
    }

    private SIPRequest callPhone(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo agentVoInfo, String caller, ProxyAuthenticateHeader header,SIPRequest sipRequest ,SIPResponse response,String callBackId, HookEvent hookEvent, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException {
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SIPRequest request = null;
        if (agentVoInfo == null) {
            return null;
        }
        if(header != null){
            //构建器
            request = (SIPRequest)SIPRequestProvider.builder(sipServer, null, Request.INVITE, sipRequest.getContent().toString())
                    .createSipURI(((SipURI) sipRequest.getToHeader().getAddress().getURI()).getUser(), String.format("%s:%s", response.getRemoteAddress().getHostAddress(),response.getRemotePort()))
                    .addViaHeader(sipRequest.getViaHost(), sipRequest.getViaPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(null,null,sipRequest.getCallId().getCallId())
                    .createFromHeader(sipRequest.getFromHeader())
                    .createToHeader(sipRequest.getToHeader())
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createContentTypeHeader("application", "sdp")
                    .createProxyAuthenticateHeader(Request.INVITE, agentVoInfo.getSipPhone(), SipUtils.getUserIdToHeader(sipRequest), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()), agentVoInfo.getPasswd(), header)
                    .createContactHeader(agentVoInfo.getCalled(),String.format("%s:%s", sipRequest.getViaHost(),sipRequest.getViaPort()))
                    .createUserAgentHeader()
                    .buildRequest();
            SipSendMessage.sendMessage(sipServer,agentVoInfo, request,null,null);
        }else if(StringUtils.isNotEmpty(callBackId)){//在对方拨打时回接时 触发
            //添加流变动回调
            if(hookEvent != null){
                HookKey audioHookKey = HookKeyFactory.onStreamChanged("rtp", audioSsrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                mediaHookSubscribe.addSubscribe(audioHookKey,(MediaServerVo mediaServer, HookVo res)->{
                    res.setCallId(callBackId);
                    hookEvent.response(mediaServer,res);
                    mediaHookSubscribe.removeSubscribe(audioHookKey);
                });
                if(videoSsrcInfo != null){
                    HookKey videoHookKey = HookKeyFactory.onStreamChanged("rtp", videoSsrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                    mediaHookSubscribe.addSubscribe(videoHookKey,(MediaServerVo mediaServer, HookVo res)->{
                        res.setCallId(callBackId);
                        hookEvent.response(mediaServer,res);
                        mediaHookSubscribe.removeSubscribe(videoHookKey);
                    });
                }
            }
            //提前缓存，后续推流时需要
            ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callBackId,"rtp",audioSsrcInfo.getStream(), audioSsrcInfo.getSsrc(), mediaServerVo.getId(),null, VideoStreamType.CALL_AUDIO_PHONE);
            if(videoSsrcInfo != null){
                ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callBackId,"rtp",videoSsrcInfo.getStream(), videoSsrcInfo.getSsrc(), mediaServerVo.getId(),null, VideoStreamType.CALL_VIDEO_PHONE);
            }
            SipSendMessage.sendMessage(sipServer,agentVoInfo, callBackId,(handle)->{
                //触发 INVITE 请求回调，开始继续下步流程
                String key = String.format("%s%s", AgentSubscribeHandle.VIDEO_AGENT_OK_EVENT_SUBSCRIBE_MANAGER, callBackId);
                RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new RestResultEvent(RespCode.CODE_0.getValue(),String.format("坐席:[%S]接听电话操作",agentVoInfo.getAgentKey()),null)));
            },(ok)->{
                SIPMessage message = null;
                if(ok.getEvent() instanceof ResponseEvent){
                    ResponseEvent event = (ResponseEvent) ok.getEvent();
                     message = (SIPMessage)event.getResponse();
                }else if(ok.getEvent() instanceof RequestEvent){
                    RequestEvent event = (RequestEvent) ok.getEvent();
                    message = (SIPMessage)event.getRequest();
                }
                // 这里为例避免一个通道的点播多次点播只有一个callID这个参数使用一个固定值
                ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callBackId,"rtp",audioSsrcInfo.getStream(), audioSsrcInfo.getSsrc(), mediaServerVo.getId(),message, VideoStreamType.CALL_AUDIO_PHONE);
                if(videoSsrcInfo != null){
                    ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callBackId,"rtp",videoSsrcInfo.getStream(), videoSsrcInfo.getSsrc(), mediaServerVo.getId(),message, VideoStreamType.CALL_VIDEO_PHONE);
                }
                okEvent.response(ok);
            },(error)->{
                RedisService.getSendRtpManager().deleteSendRTPServer(agentVoInfo.getAgentKey(),agentVoInfo.getAgentKey(),callBackId);
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream(),callBackId,null);
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
                if(videoSsrcInfo != null){
                    ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),videoSsrcInfo.getStream(),callBackId,null);
                    ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),videoSsrcInfo.getSsrc());
                }
                errorEvent.response(error);
                //发送报错
                String key = String.format("%s%s", AgentSubscribeHandle.VIDEO_AGENT_ERROR_EVENT_SUBSCRIBE_MANAGER, callBackId);
                RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new RestResultEvent(RespCode.CODE_2.getValue(),error.getMsg(),null)));
            });
        }else {
            String content = createSdp(sipServer, mediaServerVo, videoSsrcInfo,audioSsrcInfo, agentVoInfo);
            // f字段:f= v/编码格式/分辨率/帧率/码率类型/码率大小a/编码格式/码率大小/采样率
            // content.append("f=v/2/5/25/1/4000a/1/8/1" + "\r\n"); // 未发现支持此特性的设备
            String localIp = sipServer.getLocalIp(agentVoInfo.getFsHost());
            SipConfigProperties sipConfigProperties = sipServer.getSipConfigProperties();
            //构建器
            request = (SIPRequest)SIPRequestProvider.builder(sipServer, null, Request.INVITE, content.toString())
                    .createSipURI(caller, String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()))
                    .addViaHeader(localIp, sipConfigProperties.getPort(), TransportType.UDP.getName(), true)
                    .createCallIdHeader(localIp,TransportType.getName(agentVoInfo.getTransport()),null)
                    .createFromHeader(agentVoInfo.getCalled(), agentVoInfo.getFsHost(), SipUtils.getNewFromTag())
                    .createToHeader(caller, agentVoInfo.getFsHost(), null)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createContentTypeHeader("application", "sdp")
                    .createContactHeader(agentVoInfo.getCalled(),String.format("%s:%s",localIp, sipConfigProperties.getPort()))
                    .createUserAgentHeader()
                    .buildRequest();
            String callId = request.getCallId().getCallId();
            //添加流变动回调
            if(hookEvent != null){
                HookKey audioHookKey = HookKeyFactory.onStreamChanged("rtp", audioSsrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                mediaHookSubscribe.addSubscribe(audioHookKey,(MediaServerVo mediaServer, HookVo res)->{
                    res.setCallId(callId);
                    hookEvent.response(mediaServer,res);
                    mediaHookSubscribe.removeSubscribe(audioHookKey);
                });
                if(videoSsrcInfo != null){
                    HookKey videoHookKey = HookKeyFactory.onStreamChanged("rtp", videoSsrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                    mediaHookSubscribe.addSubscribe(videoHookKey,(MediaServerVo mediaServer, HookVo res)->{
                        res.setCallId(callId);
                        hookEvent.response(mediaServer,res);
                        mediaHookSubscribe.removeSubscribe(videoHookKey);
                    });
                }
            }
            //提前缓存，后续推流时需要
            ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callId,"rtp",audioSsrcInfo.getStream(), audioSsrcInfo.getSsrc(), mediaServerVo.getId(),null, VideoStreamType.CALL_AUDIO_PHONE);
            if(videoSsrcInfo != null){
                ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callId,"rtp",videoSsrcInfo.getStream(), videoSsrcInfo.getSsrc(), mediaServerVo.getId(),null, VideoStreamType.CALL_VIDEO_PHONE);
            }
            SipSendMessage.sendMessage(sipServer,agentVoInfo, request,(ok)->{
                ResponseEvent event = (ResponseEvent) ok.getEvent();
                // 这里为例避免一个通道的点播多次点播只有一个callID这个参数使用一个固定值
                ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callId,"rtp",audioSsrcInfo.getStream(), audioSsrcInfo.getSsrc(), mediaServerVo.getId(),(SIPMessage) event.getResponse(), VideoStreamType.CALL_AUDIO_PHONE);
                if(videoSsrcInfo != null){
                    ssrcTransactionManager.put(agentVoInfo.getAgentKey(),callId,"rtp",videoSsrcInfo.getStream(), videoSsrcInfo.getSsrc(), mediaServerVo.getId(),(SIPMessage) event.getResponse(), VideoStreamType.CALL_VIDEO_PHONE);
                }
                okEvent.response(ok);
            },(error)->{
                RedisService.getSendRtpManager().deleteSendRTPServer(agentVoInfo.getAgentKey(),agentVoInfo.getAgentKey(),callId);
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream(),callId,null);
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
                if(videoSsrcInfo != null){
                    ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),videoSsrcInfo.getStream(),callId,null);
                    ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),videoSsrcInfo.getSsrc());
                }
                errorEvent.response(error);
            });
        }
        return request;
    }

    @Override
    public String createSdp(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo agentVoInfo){
        //创建发流端口
        boolean tcp = Arrays.asList("TCP-PASSIVE","TCP-ACTIVE").contains(StreamModeType.getName(agentVoInfo.getStreamMode()));
        boolean tcpActive= false;
        if(tcp){
            tcpActive = "TCP-ACTIVE".equals(StreamModeType.getName(agentVoInfo.getStreamMode()));
        }
        if(videoSsrcInfo != null){
            log.info("分配的ZLM为: {}：{} [音频：{}:{}],[视频：{}:{}]", mediaServerVo.getId(), mediaServerVo.getSdpIp(), audioSsrcInfo.getStream(), audioSsrcInfo.getPort(), videoSsrcInfo.getStream(), videoSsrcInfo.getPort());
        }else {
            log.info("分配的ZLM为: {}：{} [音频：{}:{}]", mediaServerVo.getId(), mediaServerVo.getSdpIp(), audioSsrcInfo.getStream(), audioSsrcInfo.getPort());
        }
        String cname= RandomUtil.randomString(16);
        String sdpIp =mediaServerVo.getSdpIp();
        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append(String.format("o=- %s %s IN IP4 %s %s",sipServer.getSipConfigProperties().getDomain(),sipServer.getSipConfigProperties().getDomain(),sdpIp,"\r\n"));
        content.append("s=pjmedia\r\n");
        content.append("t=0 0\r\n");
        if(tcpActive){
            content.append("m=audio " + audioSsrcInfo.getPort() + " TCP/RTP/AVP 8 101\r\n");
        }else if(tcp){
            content.append("m=audio " + audioSsrcInfo.getPort() + " TCP/RTP/AVP 8 101\r\n");
        }else {
            content.append("m=audio " + audioSsrcInfo.getPort() + " RTP/AVP 8 101\r\n");
        }
        content.append("c=IN IP4 " + sdpIp + "\r\n");
        // 某些需要rtcp 默认是发送端口+1 https://github.com/ZLMediaKit/ZLMediaKit/issues/1597
        content.append("a=rtcp:" + (audioSsrcInfo.getPort() + 1) + " IN IP4 " + sdpIp + "\r\n");
        content.append("a=sendrecv\r\n");//sendrecv 双向传输：（发送和接收）  recvonly：接受  sendonly：发送
        content.append("a=rtpmap:8 PCMA/8000\r\n");
        content.append("a=rtpmap:101 telephone-event/8000\r\n");
        content.append("a=fmtp:101 0-16\r\n");
        if (tcpActive) { // tcp主动模式
            content.append("a=setup:active\r\n");
            content.append("a=connection:new\r\n");
        }else if(tcp){
            content.append("a=setup:passive\r\n");
            content.append("a=connection:new\r\n");
        }
        content.append(String.format("a=ssrc:%s cname:%s\r\n",audioSsrcInfo.getSsrc(),cname));//ssrc
        if(videoSsrcInfo != null){
            if(tcpActive){
                content.append("m=video " + videoSsrcInfo.getPort() + " TCP/RTP/AVP 98\r\n");
            }else if(tcp){
                content.append("m=video " + videoSsrcInfo.getPort() + " TCP/RTP/AVP 98\r\n");
            }else {
                content.append("m=video " + videoSsrcInfo.getPort() + " RTP/AVP 98\r\n");
            }
            content.append("c=IN IP4 " + sdpIp + "\r\n");
            content.append("a=rtcp:" + (videoSsrcInfo.getPort() + 1) + " IN IP4 " + sdpIp + "\r\n");
            content.append("a=sendrecv\r\n");
            content.append("a=rtpmap:98 H264/90000\r\n");
            content.append("a=fmtp:98 profile-level-id=42e01e; packetization-mode=1\r\n");
            if (tcpActive) { // tcp主动模式
                content.append("a=setup:active\r\n");
                content.append("a=connection:new\r\n");
            }else if(tcp){
                content.append("a=setup:passive\r\n");
                content.append("a=connection:new\r\n");
            }
            content.append(String.format("a=ssrc:%s cname:%s\r\n",videoSsrcInfo.getSsrc(),cname));//ssrc
            content.append("a=rtcp-fb:* nack pli\r\n");
        }
        return content.toString();
    }
}
