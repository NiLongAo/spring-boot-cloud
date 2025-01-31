package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.impl;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response.AbstractSipResponseEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteStreamType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.DeviceRawContent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.InviteInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

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
            SIPResponse response = (SIPResponse) evt.getResponse();
            int statusCode = response.getStatusCode();
            // 未授权
            if (statusCode == Response.PROXY_AUTHENTICATION_REQUIRED) {
                String callId = response.getCallId().getCallId();
                ProxyAuthenticateHeader header = (ProxyAuthenticateHeader) response.getHeader(ProxyAuthenticateHeader.NAME);
                if (header == null) {
                    log.error("[请求拨打电话 ]，未获取认证参数 callId: {}", callId);
                    return;
                }
                SIPRequest sipRequest = RedisService.getAgentInfoManager().getCallPhone(callId);
                if (sipRequest == null) {
                    log.error("[请求拨打电话 错误 ]，未获取缓存请求值 callId: {}", callId);
                    return;
                }
                String userId = SipUtils.getUserIdFromHeader(sipRequest);
                AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().getSip(userId);
                if (agentVoInfo == null) {
                    log.error("[请求拨打电话 错误 ]，未获取客服信息 agentSip: {}", callId);
                    return;
                }
                sipCommanderForPlatform.callPhone(sipServer, agentVoInfo, header, sipRequest, response);
                return;
            } else if (statusCode != Response.OK) {
                log.error("[INVITE响应 状态码错误] statusCode: {}", statusCode);
                return;
            }
            sipCommander.sendAckMessage(sipServer, response, null, error -> {
                log.info("[请求拨打电话回复ACK]，异常: {}", error.getMsg());
            });
            //向对方推送流
            String agentSip = SipUtils.getUserIdFromHeader(response.getFromHeader());
            AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().getSip(agentSip);
            if (agentVoInfo == null) {
                log.error("[INVITE响应]坐席Sip：{},未上线", agentSip);
                return;
            }
            String agentKey = agentVoInfo.getAgentKey();
            String callId = response.getCallId().getCallId();
            // 成功响应
            MediaServerVoService mediaServerService = SipService.getMediaServerService();
            DeviceRawContent deviceRawContent = SipUtils.handleDeviceRawContent(response);
            if (deviceRawContent == null) {
                log.error("[视频语音流] agentKey : {} 未解析出 DeviceRawContent", agentKey);
                return;
            }
            SendRtp sendRtp = RedisService.getSendRtpManager().querySendRTPServer(agentKey, String.format("%s:%s", deviceRawContent.getVideoInfo() != null ? VideoStreamType.CALL_VIDEO_PHONE.getPushName() : VideoStreamType.CALL_AUDIO_PHONE.getPushName(), agentKey), null);
            if (sendRtp == null) {
                log.error("[视频语音流]坐席：{},未获取 推流信息", agentKey);
                return;
            } else if (StringUtils.isNotEmpty(sendRtp.getCallId())) {
                //此处是为了防止消息未处理完，在发送过来200请求，过滤掉
                log.error("[视频语音流] agentKey：{}，已收到200消息正在处理ACK。。。。。", agentKey);
                return;
            }
            RedisService.getSendRtpManager().deleteSendRTPServer(sendRtp.getAgentKey(), sendRtp.getPushStreamId(), sendRtp.getCallId());
            sendRtp.setCallId(callId);
            //关闭延迟超时
            dynamicTask.stop(sendRtp.getPushStreamId());
            MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(sendRtp.getMediaServerId());
            if (mediaServerVo == null) {
                log.error("[视频语音流] 流媒体[ {} ]未上线，无法发送请求", sendRtp.getMediaServerId());
                return;
            }
            InviteInfo invite = RedisService.getInviteStreamManager().getInviteInfoByDeviceAndChannel(deviceRawContent.getVideoInfo() != null ? VideoStreamType.CALL_VIDEO_PHONE.getCallName() : VideoStreamType.CALL_AUDIO_PHONE.getCallName(), agentKey);
            if (invite == null) {
                log.error("[视频语音流] agentKey : {} 未获取流播放信息", agentKey);
                return;
            }
            if (deviceRawContent.getAudioInfo() != null) {
                //关闭接收流端口，在发送流中开通此端口，然后接收流也可以使用此端口，保证收发流统一端口
                MediaClient.closeRtpServer(mediaServerVo, invite.getAudioSsrcInfo().getStream());

                SendRtp.SendRtpInfo audioInfo = sendRtp.getAudioInfo();
                audioInfo = SendRtp.createSendRtpInfo(
                        deviceRawContent.getAudioInfo().getSessionName(),
                        deviceRawContent.getAudioInfo().getAddressStr(),
                        deviceRawContent.getAudioInfo().getPort(),
                        invite.getAudioSsrcInfo().getPort(),
                        invite.getAudioSsrcInfo().getSsrc(),
                        audioInfo.getApp(),
                        audioInfo.getStreamId(),
                        invite.getAudioSsrcInfo().getStream(),
                        deviceRawContent.getAudioInfo().isMediaTransmissionTCP(),
                        deviceRawContent.getAudioInfo().isTcpActive(),
                        audioInfo.getServerId(),
                        audioInfo.isRtcp(),
                        InviteStreamType.getInviteStreamType(deviceRawContent.getAudioInfo().getSessionName().toUpperCase()));
                audioInfo.setUsePs(false);
                audioInfo.setPt(VideoStreamType.CALL_AUDIO_PHONE.getPt());
                audioInfo.setOnlyAudio(true);
                //初始化
                audioInfo.initKeepPort(mediaServerVo);
                sendRtp.setAudioInfo(audioInfo);
                if (audioInfo.getLocalPort() != 0) {
                    MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
                    //MediaClient.closeRtpServer(mediaServerVo,sendRtp.getStreamId());已关闭
                    HookKey hookKey = HookKeyFactory.onRtpServerTimeout(audioInfo.getStreamId(), mediaServerVo.getId());
                    // 订阅 zlm启动事件, 新的zlm也会从这里进入系统
                    mediaHookSubscribe.removeSubscribe(hookKey);
                }
            }
            if (deviceRawContent.getVideoInfo() != null) {
                //关闭接收流端口，在发送流中开通此端口，然后接收流也可以使用此端口，保证收发流统一端口
                MediaClient.closeRtpServer(mediaServerVo, invite.getVideoSsrcInfo().getStream());

                SendRtp.SendRtpInfo videoInfo = sendRtp.getVideoInfo();
                videoInfo = SendRtp.createSendRtpInfo(
                        deviceRawContent.getVideoInfo().getSessionName(),
                        deviceRawContent.getVideoInfo().getAddressStr(),
                        deviceRawContent.getVideoInfo().getPort(),
                        invite.getVideoSsrcInfo().getPort(),
                        invite.getVideoSsrcInfo().getSsrc(),
                        videoInfo.getApp(),
                        videoInfo.getStreamId(),
                        invite.getVideoSsrcInfo().getStream(),
                        deviceRawContent.getVideoInfo().isMediaTransmissionTCP(),
                        deviceRawContent.getVideoInfo().isTcpActive(),
                        videoInfo.getServerId(),
                        videoInfo.isRtcp(),
                        InviteStreamType.getInviteStreamType(deviceRawContent.getVideoInfo().getSessionName().toUpperCase()));
                videoInfo.setUsePs(false);
                videoInfo.setPt(VideoStreamType.CALL_VIDEO_PHONE.getPt());
                videoInfo.setOnlyAudio(false);
                //初始化
                videoInfo.initKeepPort(mediaServerVo);
                sendRtp.setVideoInfo(videoInfo);
                if (videoInfo.getLocalPort() != 0) {
                    MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
                    //MediaClient.closeRtpServer(mediaServerVo,sendRtp.getStreamId());已关闭
                    HookKey hookKey = HookKeyFactory.onRtpServerTimeout(videoInfo.getStreamId(), mediaServerVo.getId());
                    // 订阅 zlm启动事件, 新的zlm也会从这里进入系统
                    mediaHookSubscribe.removeSubscribe(hookKey);
                }
            }
            MediaRestResult restResult = MediaClient.startSendRtp(
                    mediaServerVo,
                    sendRtp
            );
            startSendRtpStreamHand(sendRtp, agentVoInfo, restResult);
            RedisService.getSendRtpManager().put(sendRtp);
        } catch (Exception e) {
            log.error("[点播回复ACK]，消息处理异常：", e);
        }
    }

    private void startSendRtpStreamHand(SendRtp sendRtpItem, AgentVoInfo agentVoInfo, MediaRestResult restResult) {
        if (restResult == null || restResult.getCode() != RespCode.CODE_0.getValue()) {
            if (restResult == null) {
                log.error("RTP推流失败: 请检查ZLM服务");
            } else {
                log.error("RTP推流失败: {}, 参数：{}", restResult.getMsg(), JSONUtil.toJsonPrettyStr(sendRtpItem));
            }
            // 向上级平台
            try {
                sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo, sendRtpItem, null, null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
            }
        }
    }
}
