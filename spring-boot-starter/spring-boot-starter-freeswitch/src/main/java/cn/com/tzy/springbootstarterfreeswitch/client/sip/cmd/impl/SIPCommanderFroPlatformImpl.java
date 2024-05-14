package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.impl;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPRequestProvider;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.SipConfigProperties;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.CharsetType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.TransportType;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.PlatformRegisterManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SipTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.ParentPlatformService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.PlatformRegisterInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SipTransactionInfo;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import java.text.ParseException;
@Log4j2
@Component
public class SIPCommanderFroPlatformImpl implements SIPCommanderForPlatform {

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
                    .createToHeader(agentVoInfo.getAgentCode(), agentVoInfo.getFsHost(), toTag)
                    .createCSeqHeader(RedisService.getCseqManager().getCSEQ())
                    .createUserAgentHeader()
                    .createContactHeader(agentVoInfo.getAgentCode(), String.format("%s:%s", deviceSipConfig.getIp(), deviceSipConfig.getPort()))
                    .createExpiresHeader(isRegister ? deviceSipConfig.getExpires() : 0)
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
                    .createExpiresHeader(isRegister ? deviceSipConfig.getExpires() : 0)
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
                .createContentTypeHeader("Application", "MANSCDP+xml")
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
}
