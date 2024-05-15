package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.build.SIPRequestProvider;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.AbstractSipResponseEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteStreamType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Vector;

/**
 * @description: 处理INVITE响应
 * 需要重新写
 */
@Log4j2
@Component
public class InviteResponseProcessor extends AbstractSipResponseEvent {

    @Resource
    private DynamicTask dynamicTask;

    @Override
    public String getMethod() {
        return Request.INVITE;
    }

    @Override
    public void process(ResponseEvent evt) {
        try {
            SIPResponse response = (SIPResponse)evt.getResponse();
            int statusCode = response.getStatusCode();
            // 未授权
            if(statusCode != Response.PROXY_AUTHENTICATION_REQUIRED){
                sipCommander.sendAckMessage(sipServer,response,null,error->{
                    log.error("[请求拨打电话回复ACK]，异常：{}",error.getMsg());
                });
                String callId = response.getCallId().getCallId();
                ProxyAuthenticateHeader header = (ProxyAuthenticateHeader)response.getHeader(ProxyAuthenticateHeader.NAME);
                if(header == null){
                    log.error("[请求拨打电话 错误 ]，未获取认证参数 callId：{}",callId);
                    return;
                }
                SIPRequest sipRequest = RedisService.getAgentInfoManager().getCallPhone(callId);
                if(sipRequest == null){
                    log.error("[请求拨打电话 错误 ]，未获取缓存请求值 callId：{}",callId);
                    return;
                }
                String userId = SipUtils.getUserIdFromFromHeader(sipRequest);
                AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(userId);
                if(agentVoInfo == null){
                    log.error("[请求拨打电话 错误 ]，未获取客服信息 callId：{}",callId);
                    return;
                }
                AuthorizationHeader authorization = SIPRequestProvider.createAuthorization(sipServer.getSipFactory(), agentVoInfo.getAgentCode(), agentVoInfo.getAgentCode(), String.format("%s:%s", agentVoInfo.getFsHost(), agentVoInfo.getFsPost()), agentVoInfo.getPasswd(), header);
                sipRequest.addHeader(authorization);
                //添加认证参数再次请求拨打电话
                SipSendMessage.sendMessage(sipServer,agentVoInfo,sipRequest,null,null);
                return;
            } else if(statusCode != Response.OK){
                log.warn("[INVITE响应 状态码错误] statusCode：{}",statusCode);
                return;
            }
            // 成功响应
            ResponseEventExt event = (ResponseEventExt)evt;
            String contentString = new String(response.getRawContent());
            Gb28181Sdp gb28181Sdp = SipUtils.parseSDP(contentString);
            SessionDescription sdp = gb28181Sdp.getBaseSdb();
            sipCommander.sendAckMessage(sipServer,sdp,event,response,null,error->{
                log.error("[点播回复ACK]，异常：{}",error.getMsg());
            });
            //向对方推送流
            String agentCode = sdp.getOrigin().getUsername();
            String callId = response.getCallId().getCallId();
            SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
            MediaServerVoService mediaServerService = SipService.getMediaServerService();
            SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
            SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(agentCode, null, null, VideoStreamType.push_web_rtp);
            MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(paramOne.getMediaServerId());
            DeviceRawContent deviceRawContent = handleDeviceRawContent(mediaServerVo, response, paramOne.isOnVideo() ? "96" : "8");
            if(paramOne == null || !paramOne.isOnPush()){
                log.warn("[视频语音流]坐席：{},未获取 推流信息",agentCode);
                return;
            }else {
                ssrcTransactionManager.remove(agentCode,paramOne.getStream(),VideoStreamType.push_web_rtp);
                ssrcTransactionManager.put(agentCode,callId,paramOne.isOnPush(),paramOne.isOnVideo(),paramOne.getApp(),paramOne.getStream(),paramOne.getSsrc(),paramOne.getMediaServerId(),response,VideoStreamType.push_web_rtp);
                dynamicTask.stop(String.format("push_web_rtp:%s",agentCode));
            }

            SendRtp sendRtp = MediaClient.createSendRtp(mediaServerVo,deviceRawContent.getSessionName(), deviceRawContent.getAddressStr(), deviceRawContent.getPort(),deviceRawContent.getSsrc(),agentCode,"push_web_rtp",agentCode, deviceRawContent.isMediaTransmissionTCP(),deviceRawContent.isTcpActive(),sipServer.getVideoProperties().getServerId(),callId,true, InviteStreamType.getInviteStreamType(deviceRawContent.getSessionName().toUpperCase()));
            if (sendRtp == null) {
                log.warn("[视频语音流ACK] sendRtp is null 服务器端口资源不足");
                sipCommander.sendAckMessage(sipServer,sdp,event,response,null,error->{
                    log.error("[视频语音流ACK]，sendRtp 异常：{}",error.getMsg());
                });
                return;
            }
            sendRtp.setPt(paramOne.isOnVideo()?96:8);
            sendRtp.setUsePs(false);
            sendRtp.setOnlyAudio(paramOne.isOnVideo());

            log.info("rtp/{}开始向上级推流, 目标={}:{}，SSRC={}", sendRtp.getStreamId(), sendRtp.getIp(), sendRtp.getPort(), sendRtp.getSsrc());
            MediaRestResult restResult  = MediaClient.startSendRtp(
                    mediaServerVo,
                    "__defaultVhost__",
                    sendRtp.getApp(),
                    sendRtp.getStreamId(),
                    sendRtp.getSsrc(),
                    sendRtp.getIp(),
                    sendRtp.getPort(),
                    sendRtp.isTcp() ? "0" : "1",
                    sendRtp.getLocalPort(),
                    sendRtp.getPt(),
                    sendRtp.isUsePs() ? 1 : 0,
                    sendRtp.isOnlyAudio() ? 1 : 0,
                    sendRtp.isTcp()?(sendRtp.isRtcp() ? 1 : 0):null
            );
            if(restResult != null){
                startSendRtpStreamHand(sendRtp,agentCode, restResult);
            }
            sendRtpManager.put(sendRtp);
        } catch (Exception e){
            log.error("[点播回复ACK]，消息处理异常：", e );
        }
    }

    private DeviceRawContent handleDeviceRawContent(MediaServerVo mediaServerVo, SIPResponse response, String mediaFormat) throws SdpException {
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();

        // 解析sdp消息, 使用jainsip 自带的sdp解析方式
        String contentString = new String(response.getRawContent());
        Gb28181Sdp gb28181Sdp = SipUtils.parseSDP(contentString);
        SessionDescription sdp = gb28181Sdp.getBaseSdb();
        String sessionName = sdp.getSessionName().getValue();
        //  获取支持的格式
        Vector mediaDescriptions = sdp.getMediaDescriptions(true);
        // 查看是否支持PS 负载96
        //String ip = null;
        int port = -1;
        String downloadSpeed = "1";
        boolean mediaTransmissionTCP = false;
        boolean tcpActive = false;
        for (Object description : mediaDescriptions) {
            MediaDescription mediaDescription = (MediaDescription) description;
            Media media = mediaDescription.getMedia();
            downloadSpeed = mediaDescription.getAttribute("downloadspeed");
            Vector mediaFormats = media.getMediaFormats(false);
            if (mediaFormats.contains(mediaFormat)) {
                port = media.getMediaPort();
                //String mediaType = media.getMediaType();
                String protocol = media.getProtocol();
                // 区分TCP发流还是udp， 当前默认udp
                if ("TCP/RTP/AVP".equalsIgnoreCase(protocol)) {
                    String setup = mediaDescription.getAttribute("setup");
                    if (setup != null) {
                        mediaTransmissionTCP = true;
                        if ("active".equalsIgnoreCase(setup)) {
                            tcpActive = true;
                        } else if ("passive".equalsIgnoreCase(setup)) {
                            tcpActive = false;
                        }
                    }
                }
                break;
            }
        }
        if (port == -1) {
            log.info("不支持的媒体格式，返回415");
            // 回复不支持的格式
            try {
                sipCommander.sendAckMessage(sipServer,response,null,error->{
                    log.error("[请求拨打电话回复ACK]，异常：{}",error.getMsg());
                });
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite 不支持的格式: {}", e.getMessage());
            }
            return null;
        }
        String ssrc;
        if (sipServer.getVideoProperties().getUseCustomSsrcForParentInvite() || gb28181Sdp.getSsrc() == null) {
            // 上级平台点播时不使用上级平台指定的ssrc，使用自定义的ssrc，参考国标文档-点播外域设备媒体流SSRC处理方式
            ssrc = "Play".equalsIgnoreCase(sessionName) ? ssrcConfigManager.getPlaySsrc(mediaServerVo.getId()) : ssrcConfigManager.getPlayBackSsrc(mediaServerVo.getId());
        }else {
            ssrc = gb28181Sdp.getSsrc();
        }
        String username = sdp.getOrigin().getUsername();
        String addressStr = sdp.getOrigin().getAddress();
        return DeviceRawContent.builder()
                .username(username)
                .addressStr(addressStr)
                .downloadSpeed(downloadSpeed)
                .port(port)
                .ssrc(ssrc)
                .mediaTransmissionTCP(mediaTransmissionTCP)
                .tcpActive(tcpActive)
                .sessionName(sessionName)
                .build();
    }

    private void startSendRtpStreamHand(SendRtp sendRtpItem, String agentCode, MediaRestResult restResult) {
        if (restResult == null || restResult.getCode() != RespCode.CODE_0.getValue()) {
            sendRtpItem.setStatus(3);
            if(restResult == null){
                log.error("RTP推流失败: 请检查ZLM服务");
            }else {
                log.error("RTP推流失败: {}, 参数：{}",restResult.getMsg(), JSONUtil.toJsonPrettyStr(sendRtpItem));
            }
            AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentCode);
            if(agentVoInfo != null){
                // 向上级平台
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtpItem,null,null);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
                }
            }
        } else {
            sendRtpItem.setStatus(2);
            log.info("调用ZLM推流接口, 结果： {}",  restResult.toString());
            log.info("RTP推流成功[ {}/{} ]，{}->{}:{}, " ,sendRtpItem.getApp(), sendRtpItem.getStreamId(), sendRtpItem.getLocalPort(), sendRtpItem.getIp(), sendRtpItem.getPort());
        }
    }
}
