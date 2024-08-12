package cn.com.tzy.springbootfs.service.fs.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.excption.RespException;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.dome.fs.Agent;
import cn.com.tzy.springbootfs.mapper.fs.AgentMapper;
import cn.com.tzy.springbootfs.service.fs.AgentService;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.callback.InviteErrorCallback;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.common.interfaces.ResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteSessionStatus;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.InviteStreamManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.enums.InviteErrorCode;
import cn.com.tzy.springbootstartervideobasic.enums.StreamModeType;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

@Log4j2
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService{

    @Resource
    private AgentVoService agentVoService;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private MediaHookSubscribe mediaHookSubscribe;
    @Resource
    protected SipServer sipServer;
    @Override
    public Agent findUserId(Long userId) {
        return baseMapper.findUserId(userId);
    }

    @Override
    public UserModel findUserModel(String sip) {
        return baseMapper.findUserModel(sip);
    }

    @Override
    public void login(String agentKey, ResultEvent event) {
        AgentVoInfo agentVoInfo = agentVoService.getAgentByKey(agentKey);
        if(agentVoInfo == null){
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_2.getValue(),"未获取客服信息"));
            }
            return;
        }
        SipService.getParentPlatformService().login(agentVoInfo,ok->{
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_0.getValue(),"登陆成功"));
            }
        },error->{
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            }
        });
    }

    @Override
    public void logout(String agentKey, ResultEvent event) {
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if(agentVoInfo == null){
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_2.getValue(),"未获取客服信息"));
            }
            return;
        }
        SipService.getParentPlatformService().unregister(agentVoInfo,ok->{
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_0.getValue(),"退出成功"));
            }
        },error->{
            if(event!=null){
                event.result(RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
            }
        });
    }

    @Override
    public RestResult<?> stopStream(String callId) {
        List<SsrcTransaction> paramAll = RedisService.getSsrcTransactionManager().getParamAll(null, callId, null, null);
        if(paramAll == null || paramAll.isEmpty()){
            return RestResult.result(RespCode.CODE_0);
        }
        for (SsrcTransaction ssrcTransaction : paramAll) {
            AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(ssrcTransaction.getAgentKey());
            if(agentVoInfo != null){
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,null,null,callId,null,null,null);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败]  发送BYE 关闭客服 : {} 通话流报错 : {}",ssrcTransaction.getAgentKey(), e.getMessage());
                }
            }
        }
        return RestResult.result(RespCode.CODE_0);
    }

    /**
     * 拨打电话
     */
    @Override
    public void callPhone(VideoStreamType type,SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentBySip,String caller, String ssrc,String callBackId,  InviteErrorCallback<Object> callback) {
        if(mediaServerVo == null){
            throw new RespException(RespCode.CODE_2.getValue(),"未找到可用的zlm");
        }
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        //获取播放流
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByDeviceAndChannel(null,agentBySip.getAgentKey());
        if(inviteInfo != null){
            if (inviteInfo.getStreamInfo() == null) {
                log.info("inviteInfo 已存在， StreamInfo 不存在，添加回调等待");
                // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                inviteStreamManager.once(inviteInfo.getType(), agentBySip.getAgentKey(), null, callback);
                return ;
            }else {
                StreamInfo streamInfo = inviteInfo.getStreamInfo();
                String streamId = streamInfo.getStream();
                if (streamId == null) {
                    callback.run(InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(), "拨打电话失败， redis缓存streamId等于null", null);
                    inviteStreamManager.call(inviteInfo.getType(), agentBySip.getAgentKey(), null,
                            InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(),
                            "拨打电话失败， redis缓存streamId等于null",
                            null);
                    return ;
                }
                MediaServerVo vo = SipService.getMediaServerService().findOnLineMediaServerId(streamInfo.getMediaServerId());
                if (vo == null) {
                    callback.run(InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(), "拨打电话失败， 流媒体未上线", null);
                    inviteStreamManager.call(inviteInfo.getType(), agentBySip.getAgentKey(), null,
                            InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(),
                            "拨打电话失败， 流媒体未上线",
                            null);
                    return ;
                }
                MediaRestResult mediaList = MediaClient.getMediaList(vo, "__defaultVhost__", null, "rtp", streamId);
                if(mediaList != null && mediaList.getCode() == RespCode.CODE_0.getValue() && ObjectUtil.isNotEmpty(mediaList.getData())){
                    callback.run(InviteErrorCode.SUCCESS.getCode(), InviteErrorCode.SUCCESS.getMsg(), streamInfo);
                    inviteStreamManager.call(inviteInfo.getType(), agentBySip.getAgentKey(), null,
                            InviteErrorCode.SUCCESS.getCode(),
                            InviteErrorCode.SUCCESS.getMsg(),
                            streamInfo);
                    return ;
                }else {
                    // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                    inviteStreamManager.once(inviteInfo.getType(), agentBySip.getAgentKey(), null, callback);
                    FsService.getAgentService().stopPlay(agentBySip.getAgentKey());
                    inviteStreamManager.removeInviteInfoByDeviceAndChannel(inviteInfo.getType(), agentBySip.getAgentKey());
                }
            }
        }
        //视频电话开启视频电话端口 相关
        SSRCInfo videoSsrcInfo = null,audioSsrcInfo = null;
        if(type == VideoStreamType.CALL_VIDEO_PHONE){
            String streamId = String.format("%s:%s",VideoStreamType.CALL_VIDEO_PHONE.getName(),agentBySip.getAgentKey());
            //开启视频播放相关信息
            videoSsrcInfo = MediaClient.openRTPServer(mediaServerVo, streamId, ssrc, agentBySip.getSsrcCheck() == ConstEnum.Flag.YES.getValue(), false, 0,false,agentBySip.getStreamMode());
            if(videoSsrcInfo == null){
                callback.run(InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(), null);
                inviteStreamManager.call(type, agentBySip.getAgentKey(), null,
                        InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(),
                        InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(),
                        null);
                return;
            }
        }
        // 拨打电话开始
        String streamId = String.format("%s:%s",VideoStreamType.CALL_AUDIO_PHONE.getName(),agentBySip.getAgentKey());
        //开启音频播放相关信息
        audioSsrcInfo = MediaClient.openRTPServer(mediaServerVo, streamId, ssrc, agentBySip.getSsrcCheck() == ConstEnum.Flag.YES.getValue(), false, 0,false,agentBySip.getStreamMode());
        if(audioSsrcInfo == null){
            callback.run(InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(), null);
            inviteStreamManager.call(type, agentBySip.getAgentKey(), null,
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(),
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(),
                    null);
            return;
        }
        callPhone(type,sipServer, mediaServerVo,videoSsrcInfo,audioSsrcInfo, agentBySip,caller,callBackId,callback);
    }



    /**
     * 电话实时流
     */
    private void callPhone(VideoStreamType type,SipServer sipServer, MediaServerVo mediaServerVo,SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo agentVoInfo, String caller,String callBackId, InviteErrorCallback<Object> callback) {
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();

        log.info("[拨打电话开始] agentCode: {}, 收流端口：{}, 收流模式：{}, SSRC: {}, SSRC校验：{}", agentVoInfo.getAgentKey(), audioSsrcInfo.getPort(), agentVoInfo.getStreamMode(), audioSsrcInfo.getSsrc(), agentVoInfo.getSsrcCheck());
        if(audioSsrcInfo.getPort() <= 0 || (videoSsrcInfo != null && videoSsrcInfo.getPort() <= 0)){
            log.error("[点播端口分配异常]，agentCode={},audioSsrcInfo={}", agentVoInfo.getAgentKey(), audioSsrcInfo);
            //释放 ssrc
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream());
            if(videoSsrcInfo !=null){
                //释放 ssrc
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),videoSsrcInfo.getSsrc());
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),videoSsrcInfo.getStream());
            }

            callback.run(InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), "点播端口分配异常", null);
            inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), "点播端口分配异常", null);
            return;
        }
        // 初始化redis中的invite消息状态
        InviteInfo inviteInfo = new InviteInfo(ObjectUtil.defaultIfNull(JwtUtils.getUserId(), VideoConstant.DEFAULT_DOWNLOAD_USER),agentVoInfo.getAgentKey(), videoSsrcInfo,audioSsrcInfo, mediaServerVo.getSdpIp(), agentVoInfo.getStreamMode(), type, InviteSessionStatus.ready);
        inviteStreamManager.updateInviteInfo(inviteInfo);
        //超时任务处理
        String timeOutTaskKey = RandomUtil.randomString(32);
        dynamicTask.startDelay(timeOutTaskKey,sipServer.getVideoProperties().getPlayTimeout(),()->{
            // 执行超时任务时查询是否已经成功，成功了则不执行超时任务，防止超时任务取消失败的情况
            InviteInfo inviteInfoForTimeOut = inviteStreamManager.getInviteInfoByDeviceAndChannel(type, agentVoInfo.getAgentKey());
            if(inviteInfoForTimeOut == null || inviteInfoForTimeOut.getStreamInfo() == null){
                log.info("[点播超时] 收流超时 agentCode: {},，端口：{}, SSRC: {}", agentVoInfo.getAgentKey(), audioSsrcInfo.getPort(), audioSsrcInfo.getSsrc());
                // 点播超时回复BYE 同时释放ssrc以及此次点播的资源
                callback.run(InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getCode(), InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamManager.call(type,  agentVoInfo.getAgentKey(), null, InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getCode(), InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamManager.removeInviteInfoByDeviceAndChannel(type, agentVoInfo.getAgentKey());
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,audioSsrcInfo.getStream(),videoSsrcInfo==null?null:videoSsrcInfo.getStream(),null,null,null,null);
                } catch (InvalidArgumentException | SipException | ParseException e) {
                    log.error("[点播超时]， 发送BYE失败 {}", e.getMessage());
                }finally {
                    ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
                    ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream());
                    MediaClient.closeRtpServer(mediaServerVo,audioSsrcInfo.getStream());
                    if(videoSsrcInfo != null){
                        MediaClient.closeRtpServer(mediaServerVo,videoSsrcInfo.getStream());
                    }
                    // 取消订阅消息监听
                    HookKey hookKey = HookKeyFactory.onStreamChanged("rtp", audioSsrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                    mediaHookSubscribe.removeSubscribe(hookKey);
                }
            }
        });
        try {
            SIPRequest rtp = sipCommanderForPlatform.callPhone(sipServer, mediaServerVo,videoSsrcInfo, audioSsrcInfo, agentVoInfo,caller, callBackId,(media, response) -> {
                log.info("收到订阅消息： " + JSONUtil.toJsonStr(response));
                dynamicTask.stop(timeOutTaskKey);
                OnStreamChangedHookVo vo = (OnStreamChangedHookVo) response;
                StreamInfo streamInfo = new StreamInfo(mediaServerVo, "rtp", vo.getStream(), vo.getTracks(), null, null, agentVoInfo.getAgentKey());
                InviteInfo invite = inviteStreamManager.getInviteInfoByDeviceAndChannel(type, agentVoInfo.getAgentKey());
                if (invite != null) {
                    invite.setStatus(InviteSessionStatus.ok);
                    invite.setStreamInfo(streamInfo);
                    inviteStreamManager.updateInviteInfo(invite);
                }
                callback.run(InviteErrorCode.SUCCESS.getCode(), InviteErrorCode.SUCCESS.getMsg(), streamInfo);
                inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                        InviteErrorCode.SUCCESS.getCode(),
                        InviteErrorCode.SUCCESS.getMsg(),
                        streamInfo);
                //返回成功流信息
                //可获取视频截图
            }, (okEvent) -> {
                inviteOKHandler(type,sipServer, mediaServerVo, agentVoInfo,videoSsrcInfo, audioSsrcInfo, inviteInfo, okEvent, timeOutTaskKey, callback);
            }, (errEvent) -> {
                log.error("收到订阅错误消息： " + JSONUtil.toJsonStr(errEvent));
                //发送错误后处理
                dynamicTask.stop(timeOutTaskKey);
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(), audioSsrcInfo.getSsrc());
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(), audioSsrcInfo.getStream());
                MediaClient.closeRtpServer(mediaServerVo, audioSsrcInfo.getStream());
                callback.run(InviteErrorCode.ERROR_FOR_SIGNALLING_ERROR.getCode(),
                        String.format("点播失败， 错误码： %s, %s", errEvent.getStatusCode(), errEvent.getMsg()), null);
                inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                        InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        String.format("点播失败， 错误码： %s, %s", errEvent.getStatusCode(), errEvent.getMsg()), null);

                inviteStreamManager.removeInviteInfoByDeviceAndChannel(type, agentVoInfo.getAgentKey());
            });
            if(rtp != null){
                RedisService.getAgentInfoManager().putCallPhone(rtp.getCallId().getCallId(),rtp);
            }
        } catch (InvalidArgumentException | SipException | ParseException e) {
            //发送异常处理
            log.error("[命令发送失败] 点播消息: ", e);
            dynamicTask.stop(timeOutTaskKey);
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream());
            MediaClient.closeRtpServer(mediaServerVo,audioSsrcInfo.getStream());
            callback.run(InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getCode(), InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getMsg(), null);
            inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                    InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getCode(),
                    InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getMsg(), null);
            inviteStreamManager.removeInviteInfoByDeviceAndChannel(type, agentVoInfo.getAgentKey());
        }
    }

    private void inviteOKHandler(VideoStreamType type,SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentVoInfo, SSRCInfo videoSsrcInfo,SSRCInfo audioSsrcInfo, InviteInfo inviteInfo, EventResult okEvent, String timeOutTaskKey, InviteErrorCallback<Object> callback) {
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();

        inviteInfo.setStatus(InviteSessionStatus.ok);
        String contentString = null;
        SIPMessage message = null;
        //发送成功后操作
        if( okEvent.getEvent() instanceof ResponseEvent){
            ResponseEvent responseEvent = (ResponseEvent) okEvent.getEvent();
            contentString = new String(responseEvent.getResponse().getRawContent());
            message = (SIPMessage)responseEvent.getResponse();
        }else if(okEvent.getEvent() instanceof RequestEvent){
            RequestEvent responseEvent = (RequestEvent) okEvent.getEvent();
            contentString = new String(responseEvent.getRequest().getRawContent());
            message = (SIPMessage)responseEvent.getRequest();
        }
        String ssrcInResponse = SipUtils.getSsrcFromSdp(contentString);
        // 检查是否有y字段
        //不进 此条件 fs 没有返回ssrc
        if (ssrcInResponse != null) {
            // 查询到ssrc不一致且开启了ssrc校验则需要针对处理
            if (audioSsrcInfo.getSsrc().equals(ssrcInResponse)) {
                if(mediaServerVo.getRtpEnable()==ConstEnum.Flag.NO.getValue() && agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())){
                    log.warn("[Invite 200OK] 单端口收流模式不支持tcp主动模式收流");
                }else if (agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())) {
                    tcpActiveHandler(type,agentVoInfo,contentString,mediaServerVo,audioSsrcInfo,timeOutTaskKey,callback);
                    inviteStreamManager.updateInviteInfo(inviteInfo);
                }
                return;
            }
            log.info("[invite 200] 收到, 发现下级自定义了ssrc: {}", ssrcInResponse);
            //单端口模式streamId也有变化，需要重新设置监听
            if(mediaServerVo.getRtpEnable() == ConstEnum.Flag.NO.getValue()){
                if(inviteInfo.getVideoSsrcInfo()!=null){
                    SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentKey(), null, inviteInfo.getVideoSsrcInfo().getStream(),null);
                    ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),inviteInfo.getVideoSsrcInfo().getStream());
                    ssrcTransactionManager.put(agentVoInfo.getAgentKey(),paramOne.getCallId(),"rtp",inviteInfo.getVideoSsrcInfo().getStream(),ssrcInResponse,mediaServerVo.getId(),message,paramOne.getType());
                }
                SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentKey(), null, inviteInfo.getAudioSsrcInfo().getStream(),null);
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),inviteInfo.getAudioSsrcInfo().getStream());
                ssrcTransactionManager.put(agentVoInfo.getAgentKey(),paramOne.getCallId(),"rtp",inviteInfo.getAudioSsrcInfo().getStream(),ssrcInResponse,mediaServerVo.getId(),message,paramOne.getType());
                //有问题待修复，暂不进此逻辑
                inviteStreamManager.updateInviteInfoForSSRC(inviteInfo,ssrcInResponse);
                return;
            }
            //当前服务已使用相同的 下级 ssrc 时
            log.info("[Invite 200OK] SSRC修正 {}->{}", audioSsrcInfo.getSsrc(), ssrcInResponse);
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),audioSsrcInfo.getSsrc());
            // 更新ssrc
            MediaRestResult result = MediaClient.updateRtpServerSsrc(mediaServerVo, audioSsrcInfo.getStream(), ssrcInResponse);
            if(result == null || result.getCode() != RespCode.CODE_0.getValue()){
                try {
                    log.warn("[Invite 200OK] 更新ssrc失败，停止点播 {}", agentVoInfo.getAgentKey());
                    sipCommanderForPlatform.streamByeCmd(sipServer,agentVoInfo, audioSsrcInfo.getStream(),videoSsrcInfo==null?null:videoSsrcInfo.getStream(), null,null,null,null);
                } catch (InvalidArgumentException | SipException | ParseException e) {
                    log.error("[命令发送失败] 停止点播， 发送BYE: {}", e.getMessage());
                }
                dynamicTask.stop(timeOutTaskKey);
                ssrcTransactionManager.remove(agentVoInfo.getAgentKey(),audioSsrcInfo.getStream());
                callback.run(InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        "下级自定义了ssrc,重新设置收流信息失败", null);
                inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                        InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        "下级自定义了ssrc,重新设置收流信息失败", null);
                return;
            }
            audioSsrcInfo.setSsrc(ssrcInResponse);
            inviteInfo.setAudioSsrcInfo(audioSsrcInfo);
            if (agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())) {
                tcpActiveHandler(type,agentVoInfo,contentString,mediaServerVo,audioSsrcInfo,timeOutTaskKey,callback);
            }
            inviteStreamManager.updateInviteInfo(inviteInfo);
        }
    }

    private void tcpActiveHandler(VideoStreamType type,AgentVoInfo agentVoInfo, String contentString, MediaServerVo mediaServerVo, SSRCInfo ssrcInfo, String timeOutTaskKey, InviteErrorCallback<Object> callback) {
        if (!agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())) {
            return;
        }
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        try {
            Gb28181Sdp gb28181Sdp = SipUtils.parseSDP(contentString);
            SessionDescription sdp = gb28181Sdp.getBaseSdb();
            int port = -1;
            Vector mediaDescriptions = sdp.getMediaDescriptions(true);
            for (Object description : mediaDescriptions) {
                MediaDescription mediaDescription = (MediaDescription) description;
                Media media = mediaDescription.getMedia();
                Vector mediaFormats = media.getMediaFormats(false);
                if (mediaFormats.contains("96")) {
                    port = media.getMediaPort();
                    break;
                }
            }
            log.info("[TCP主动连接对方] deviceId: {},  连接对方的地址：{}:{}, 收流模式：{}, SSRC: {}, SSRC校验：{}",agentVoInfo.getAgentKey(), sdp.getConnection().getAddress(), port, agentVoInfo.getStreamMode(), ssrcInfo.getSsrc(), agentVoInfo.getSsrcCheck());
            MediaRestResult result = MediaClient.connectRtpServer(mediaServerVo, sdp.getConnection().getAddress(), port, ssrcInfo.getStream());
            log.info("[TCP主动连接对方] 结果： {}", result);
        } catch (SdpException e) {
            log.error("[TCP主动连接对方] AgentCode: {},, 解析200OK的SDP信息失败", agentVoInfo.getAgentKey(), e);
            dynamicTask.stop(timeOutTaskKey);
            MediaClient.closeRtpServer(mediaServerVo,ssrcInfo.getStream());
            // 释放ssrc
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(), ssrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentKey(), ssrcInfo.getStream());
            callback.run(InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getCode(),
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getMsg(), null);
            inviteStreamManager.call(type, agentVoInfo.getAgentKey(), null,
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getCode(),
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getMsg(), null);
        }
    }
}
