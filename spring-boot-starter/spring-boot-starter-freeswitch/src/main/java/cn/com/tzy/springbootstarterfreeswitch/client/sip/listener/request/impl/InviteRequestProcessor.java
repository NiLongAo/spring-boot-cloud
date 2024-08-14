package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteStreamType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.DeviceRawContent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.InviteInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * SIP命令类型： INVITE请求
 */
@Log4j2
@Component
public class InviteRequestProcessor  extends AbstractSipRequestEvent implements SipRequestEvent {

    @Resource
    private DynamicTask dynamicTask;
    @Override
    public String getMethod() {
        return Request.INVITE;
    }

    /**
     * 处理invite请求
     * @param evt 请求消息
     */
    @Override
    public void process(RequestEvent evt) {
        // Invite Request消息实现，此消息一般为级联消息，上级给下级发送请求视频指令
        // 能进来的被叫都是客服

        SIPRequest request = (SIPRequest)evt.getRequest();
        try {
            responseAck(request, Response.TRYING,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
        }
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        String formUserId = SipUtils.getUserIdFromHeader(request);
        if (formUserId == null) {
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未获取主动拨打坐席编号");
            return;
        }
        CallInfo callInfo = RedisService.getCallInfoManager().findCaller(formUserId);
        if(callInfo == null){
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未获取客服拨打信息");
            return;
        }
        String toUserId = SipUtils.getUserIdToHeader(request);
        if (toUserId == null) {
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未获取坐席编号");
            return;
        }
        AgentVoInfo toAgentVoInfo = RedisService.getAgentInfoManager().getCompanyAgentId(callInfo.getCompanyId(),toUserId);
        if (toAgentVoInfo == null) {
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未获取拨打坐席信息");
            return;
        }else if(toAgentVoInfo.getAgentState() != AgentStateEnum.READY){
            try {
                responseAck(request, Response.TEMPORARILY_UNAVAILABLE,String.format("拨打客服%s中，请稍后再拨",toAgentVoInfo.getAgentState() ==  null?"未正常登录":toAgentVoInfo.getAgentState().getName()));
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
            return;
        }
        LoginTypeEnum loginType = LoginTypeEnum.getLoginType(toAgentVoInfo.getLoginType());
        if(loginType == null){
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未获取登陆方式");
            return;
        }
        //再此处理用户是否接听逻辑
        //向用户发送来电消息
        DeviceRawContent deviceRawContent =SipUtils.handleDeviceRawContent(request);
        if(deviceRawContent == null){
            this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"未解析出 DeviceRawContent");
            log.error("[视频语音流] agentKey : {} 未解析出 DeviceRawContent",toAgentVoInfo.getAgentKey());
            return;
        }
        InviteInfo invite = RedisService.getInviteStreamManager().getInviteInfoByDeviceAndChannel(deviceRawContent.getVideoInfo() != null?VideoStreamType.CALL_VIDEO_PHONE:VideoStreamType.CALL_AUDIO_PHONE, toAgentVoInfo.getAgentKey());
        if(invite != null && StringUtils.isNotEmpty(invite.getSdp())){
            //类似 心跳认证是否继续通话中
            try {
                responseSdpAck(request,invite.getSdp(),toAgentVoInfo);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,String.format("[命令发送失败] invite TRYING: %s", e.getMessage()));
            }
            return;
        }
        //通知用户 是否 接听电话
        String sentAgentInviteKey = String.format("%s_%s", "SENT_AGENT_INVITE", callIdHeader.getCallId());
        //每隔一秒就给用户发送电话
        dynamicTask.startCron(sentAgentInviteKey,0,1,()->{
            FsService.getSendAgentMessage().sendMessage(AgentStateEnum.IN_CALL_RING, toAgentVoInfo, CallMessage.builder()
                    .callId(callIdHeader.getCallId())
                    .onVideo(deviceRawContent.getVideoInfo() != null? ConstEnum.Flag.YES.getValue() :ConstEnum.Flag.NO.getValue())
                    .direction(callInfo.getDirection())
                    .callType(callInfo.getCallType())
                    .caller(callInfo.getCallerDisplay())//主叫
                    .called(callInfo.getCalledDisplay())//被叫
                    .groupId(callInfo.getGroupId())
                    .build());
        });
        //15秒未接听则超时，挂断电话
        String timeoutKey = String.format("%s_%s", "INVITE_REQUEST", callIdHeader.getCallId());
        dynamicTask.startDelay(timeoutKey,15,()->{
            log.warn("执行invite超时任务，断开电话以及向客服发送挂断电话 回复对方480");
            try {
                responseAck(request, Response.TEMPORARILY_UNAVAILABLE,"接听电话超时，电话挂断");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
            FsService.getSendAgentMessage().sendErrorMessage(toAgentVoInfo,"接听电话超时，电话挂断");
            dynamicTask.stop(sentAgentInviteKey);//关闭给用户发送消息
        });
        SipSendMessage.handleAgentEvent(sipServer, callIdHeader.getCallId(), ok->{
            dynamicTask.stop(timeoutKey);//关闭超时事件
            dynamicTask.stop(sentAgentInviteKey);//关闭给用户发送消息
            //确定接听后处理以下逻辑
            try {
                responseAck(request, Response.RINGING,null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite RINGING: {}", e.getMessage());
                String key = String.format("%s%s", SipSubscribeHandle.VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER, callIdHeader.getCallId());
                RedisUtils.redisTemplate.convertAndSend(key,SerializationUtils.serialize(evt));
                FsService.getSendAgentMessage().sendErrorMessage(toAgentVoInfo,"[命令发送失败] invite RINGING Error");
                return;
            }
            try {
                inviteHandle(deviceRawContent,request,callIdHeader,toAgentVoInfo);
            }catch (Exception e){
                this.sendErrorMessage(request,Response.TEMPORARILY_UNAVAILABLE,"拨打电话是报错。。。");
                String key = String.format("%s%s", SipSubscribeHandle.VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER, callIdHeader.getCallId());
                RedisUtils.redisTemplate.convertAndSend(key,SerializationUtils.serialize(evt));
                FsService.getSendAgentMessage().sendErrorMessage(toAgentVoInfo,e.getMessage());
                return;
            }
            //发送成功处理
            String key = String.format("%s%s", SipSubscribeHandle.VIDEO_SIP_OK_EVENT_SUBSCRIBE_MANAGER, callIdHeader.getCallId());
            RedisUtils.redisTemplate.convertAndSend(key,SerializationUtils.serialize(evt));
        },error->{
            dynamicTask.stop(timeoutKey);//关闭超时事件
            dynamicTask.stop(sentAgentInviteKey);//关闭给用户发送消息
            try {
                responseAck(request, Response.DECLINE,"客服挂断电话");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
        });

    }
    private void inviteHandle(DeviceRawContent deviceRawContent,SIPRequest request,CallIdHeader callIdHeader, AgentVoInfo agentVoInfo) throws SipException {
        //1.发送用户的视频流以及音频流
        SendRtp sendRtp = RedisService.getSendRtpManager().querySendRTPServer(agentVoInfo.getAgentKey(), String.format("%s:%s",deviceRawContent.getVideoInfo()==null? VideoStreamType.PUSH_AUDIO_RTP_STREAM.getName():VideoStreamType.PUSH_VIDEO_RTP_STREAM.getName(), agentVoInfo.getAgentKey()), null);
        if(sendRtp==null){
            sendErrorExceptionMessage(String.format("[视频语音流]坐席：%s,未获取 推流信息",agentVoInfo.getAgentKey()));
            return;
        }else if(StringUtils.isNotEmpty(sendRtp.getCallId())){
            //此处是为了防止消息未处理完，在发送过来200请求，过滤掉
            sendErrorExceptionMessage(String.format("[视频语音流] agentKey：%s，已收到200消息正在处理ACK。。。。。",agentVoInfo.getAgentKey()));
            return;
        }
        RedisService.getSendRtpManager().deleteSendRTPServer(sendRtp.getAgentKey(),sendRtp.getPushStreamId(),sendRtp.getCallId());
        sendRtp.setCallId(callIdHeader.getCallId());
        //关闭延迟超时
        dynamicTask.stop(sendRtp.getPushStreamId());
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findOnLineMediaServerId(sendRtp.getMediaServerId());
        if(mediaServerVo == null){
            sendErrorExceptionMessage(String.format("[视频语音流] 流媒体[ %s ]未上线，无法发送请求",sendRtp.getMediaServerId()));
            return;
        }
        InviteInfo invite = RedisService.getInviteStreamManager().getInviteInfoByDeviceAndChannel(deviceRawContent.getVideoInfo() != null?VideoStreamType.CALL_VIDEO_PHONE:VideoStreamType.CALL_AUDIO_PHONE, agentVoInfo.getAgentKey());
        if(invite == null){
           sendErrorExceptionMessage(String.format("[视频语音流] agentKey : %s 未获取流播放信息",agentVoInfo.getAgentKey()));
            return;
        }
        //2.接收发送过来的 视频流 音频流
        if(deviceRawContent.getAudioInfo() != null){
            //关闭接收流端口，在发送流中开通此端口，然后接收流也可以使用此端口，保证收发流统一端口
            MediaClient.closeRtpServer(mediaServerVo,invite.getAudioSsrcInfo().getStream());

            SendRtp.SendRtpInfo audioInfo = sendRtp.getAudioInfo();
            audioInfo =SendRtp.createSendRtpInfo(
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
        if(deviceRawContent.getVideoInfo() != null){
            //关闭接收流端口，在发送流中开通此端口，然后接收流也可以使用此端口，保证收发流统一端口
            MediaClient.closeRtpServer(mediaServerVo,invite.getVideoSsrcInfo().getStream());

            SendRtp.SendRtpInfo videoInfo = sendRtp.getVideoInfo();
            videoInfo =SendRtp.createSendRtpInfo(
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
        MediaRestResult restResult  = MediaClient.startSendRtp(
                mediaServerVo,
                sendRtp
        );
        startSendRtpStreamHand(sendRtp,agentVoInfo,restResult);
        RedisService.getSendRtpManager().put(sendRtp);
        //3.发送sdp
        String sdp = sipCommanderForPlatform.createSdp(sipServer, mediaServerVo, invite.getVideoSsrcInfo(), invite.getAudioSsrcInfo(), agentVoInfo);
        try {
            responseSdpAck(request,sdp,agentVoInfo);
        } catch (SipException | InvalidArgumentException | ParseException e) {
           sendErrorExceptionMessage(String.format("[命令发送失败] invite TRYING: %s", e.getMessage()));
        }
        invite.setSdp(sdp);
        RedisService.getInviteStreamManager().updateInviteInfo(invite);
        return;
    }

    private void startSendRtpStreamHand(SendRtp sendRtpItem, AgentVoInfo agentVoInfo, MediaRestResult restResult) throws SipException {
        if (restResult == null || restResult.getCode() != RespCode.CODE_0.getValue()) {
            if(restResult == null){
               sendErrorExceptionMessage(String.format("RTP推流失败: 请检查ZLM服务"));
            }else {
               sendErrorExceptionMessage(String.format("RTP推流失败: %s, 参数：%s",restResult.getMsg(), JSONUtil.toJsonPrettyStr(sendRtpItem)));
            }
            // 向上级平台
            try {
                sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtpItem,null,null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
               sendErrorExceptionMessage(String.format("[命令发送失败] 国标级联 发送BYE: %s", e.getMessage()));
            }
        }
    }


    private void sendErrorMessage(SIPRequest request, Integer status, String message){
        log.warn("无法从FromHeader的Address中{}，返回:{}",message,status);
        // 参数不全， 发400，请求错误
        try {
            responseAck(request, Response.TEMPORARILY_UNAVAILABLE,message);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] invite BAD_REQUEST: {}", e.getMessage());
        }
    }

    private void sendErrorExceptionMessage(String message) throws SipException {
        log.error(message);
        throw new SipException(message);
    }
}
