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
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteSessionStatus;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.UserModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.result.DeferredResultHolder;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.InviteStreamManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookKey;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.HookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.FsRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.enums.InviteErrorCode;
import cn.com.tzy.springbootstartervideobasic.enums.StreamModeType;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Vector;

@Log4j2
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService{

    @Resource
    private DeferredResultHolder deferredResultHolder;
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
    @Resource
    protected SIPCommander sipCommander;

    @Override
    public UserModel findUserModel(String agentCode) {
        return baseMapper.findUserModel(agentCode);
    }

    @Override
    public DeferredResult<RestResult<?>> login(Agent entity) {
        String key = String.format("%s%s", DeferredResultHolder.AGENT_LOGIN,entity.getAgentCode());
        String uuid = RandomUtil.randomString(32);
        FsRestResult<RestResult<?>> result = new FsRestResult<>(8000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        if(deferredResultHolder.exist(key,null)){
            return result;
        }
        deferredResultHolder.put(key,uuid,result);

        AgentVoInfo agentVoInfo = agentVoService.getAgentBySip(entity.getAgentCode());
        if(agentVoInfo == null){
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"客服账号错误"));
            return result;
        }else if(!ObjectUtil.equals(agentVoInfo.getPasswd(),entity.getPasswd())){
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"客服密码错误"));
            return result;
        }
        SipService.getParentPlatformService().login(agentVoInfo,ok->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_0.getValue(),"登陆成功"));
        },error->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),error.getMsg()));
        });
        return result;
    }


    /**
     * 生成语音视频流，再对方回复来后，需要给对方推送
     * 获取推流地址
     * @param status 1.音频 2.视频
     */
    @Override
    public RestResult<?> pushPath(String agentCode, Integer status) {
        if(status != 2){
            return RestResult.result(RespCode.CODE_2.getValue(),"暂不支持此类型");
        }
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
        SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(agentCode, null, null, VideoStreamType.push_web_rtp);
        if(paramOne != null){
            if(StringUtils.isEmpty(paramOne.getCallId())){
                return RestResult.result(RespCode.CODE_2.getValue(),"语音流正在开启中");
            }if(agentCode.equals(paramOne.getAgentCode())){
                return RestResult.result(RespCode.CODE_2.getValue(),"语音通话中,请稍后");
            }else {
                return RestResult.result(RespCode.CODE_2.getValue(),"每个设备只能一个通道语音对讲");
            }
        }
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentCode);
        if(agentVoInfo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取客服信息");
        }
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentVoInfo);
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取流媒体信息");
        }
        String ssrc = ssrcConfigManager.getPlaySsrc(mediaServerVo.getId());
        ssrcTransactionManager.put(agentCode,null,false,!(status==1),"push_web_rtp", agentCode,ssrc,mediaServerVo.getId(),null,VideoStreamType.push_web_rtp);
        String key = String.format("push_web_rtp:%s", agentCode);
        dynamicTask.startDelay(key,15,()->{
            SsrcTransaction param = ssrcTransactionManager.getParamOne(agentCode, null, null, VideoStreamType.push_web_rtp);
            if(StringUtils.isEmpty(param.getCallId())){// 如何没有callId表示没有接收到Invite请求 则直接关闭
                this.stopAudioPushStatus(agentVoInfo);
            }
        });
        String app = "push_web_rtp";
        //开始拨打电话
        HookKey hookKey = HookKeyFactory.onStreamChanged("push_web_rtp", agentCode, true, "rtsp", mediaServerVo.getId());
        mediaHookSubscribe.addSubscribe(hookKey,(MediaServerVo mediaServer, HookVo response)->{
            mediaHookSubscribe.removeSubscribe(hookKey);
            ssrcTransactionManager.put(agentCode,null,true,!(status==1),"push_web_rtp", agentCode,ssrc,mediaServerVo.getId(),null,VideoStreamType.push_web_rtp);
            dynamicTask.stop(key);
        });
        String audioPushPath = String.format("%s://%s:%s/%s/index/api/webrtc?app=%s&stream=%s&type=push",mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?"http":"https",mediaServerVo.getStreamIp(), mediaServerVo.getSslStatus()== ConstEnum.Flag.NO.getValue()?mediaServerVo.getHttpPort():mediaServerVo.getHttpSslPort(), StringUtils.isNotEmpty(mediaServerVo.getVideoHttpPrefix())?mediaServerVo.getVideoHttpPrefix():"",app, agentCode);
        return RestResult.result(RespCode.CODE_0.getValue(),null,audioPushPath);
    }

    /**
     * 拨打电话
     */
    @Override
    public SSRCInfo callPhone(SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentBySip, String ssrc, InviteErrorCallback<Object> callback) {
        if(mediaServerVo == null){
            throw new RespException(RespCode.CODE_2.getValue(),"未找到可用的zlm");
        }
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        //获取播放流
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByDeviceAndChannel(VideoStreamType.call_phone,agentBySip.getAgentCode());
        if(inviteInfo != null){
            if (inviteInfo.getStreamInfo() == null) {
                log.info("inviteInfo 已存在， StreamInfo 不存在，添加回调等待");
                // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                inviteStreamManager.once(VideoStreamType.call_phone, agentBySip.getAgentCode(), null, callback);
                return inviteInfo.getSsrcInfo();
            }else {
                StreamInfo streamInfo = inviteInfo.getStreamInfo();
                String streamId = streamInfo.getStream();
                if (streamId == null) {
                    callback.run(InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(), "拨打电话失败， redis缓存streamId等于null", null);
                    inviteStreamManager.call(VideoStreamType.call_phone, agentBySip.getAgentCode(), null,
                            InviteErrorCode.ERROR_FOR_CATCH_DATA.getCode(),
                            "拨打电话失败， redis缓存streamId等于null",
                            null);
                    return inviteInfo.getSsrcInfo();
                }
                MediaServerVo vo = SipService.getMediaServerService().findOnLineMediaServerId(streamInfo.getMediaServerId());
                MediaRestResult mediaList = MediaClient.getMediaList(vo, "__defaultVhost__", null, "rtp", streamId);
                if(mediaList != null && mediaList.getCode() == RespCode.CODE_0.getValue() && ObjectUtil.isNotEmpty(mediaList.getData())){
                    callback.run(InviteErrorCode.SUCCESS.getCode(), InviteErrorCode.SUCCESS.getMsg(), streamInfo);
                    inviteStreamManager.call(VideoStreamType.call_phone, agentBySip.getAgentCode(), null,
                            InviteErrorCode.SUCCESS.getCode(),
                            InviteErrorCode.SUCCESS.getMsg(),
                            streamInfo);
                    return inviteInfo.getSsrcInfo();
                }else {
                    // 点播发起了但是尚未成功, 仅注册回调等待结果即可
                    inviteStreamManager.once(VideoStreamType.call_phone, agentBySip.getAgentCode(), null, callback);
                    FsService.getAgentService().stopPlay(agentBySip.getAgentCode());
                    inviteStreamManager.removeInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentBySip.getAgentCode());
                }
            }
        }
        // 拨打电话开始
        String streamId = agentBySip.getAgentCode();
        //开启播放相关信息
        SSRCInfo ssrcInfo = MediaClient.openRTPServer(mediaServerVo, streamId, ssrc, agentBySip.getSsrcCheck() == ConstEnum.Flag.YES.getValue(), false, 0,false,agentBySip.getStreamMode());
        if(ssrcInfo == null){
            callback.run(InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(), null);
            inviteStreamManager.call(VideoStreamType.call_phone, agentBySip.getAgentCode(), null,
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(),
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getMsg(),
                    null);
            return null;
        }
        callPhone(sipServer, mediaServerVo,ssrcInfo, agentBySip,callback);
        return null;
    }

    /**
     * 电话实时流
     */
    private void callPhone(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo ssrcInfo, AgentVoInfo agentVoInfo, InviteErrorCallback<Object> callback) {
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        log.info("[拨打电话开始] agentCode: {}, 收流端口：{}, 收流模式：{}, SSRC: {}, SSRC校验：{}", agentVoInfo.getAgentCode(), ssrcInfo.getPort(), agentVoInfo.getStreamMode(), ssrcInfo.getSsrc(), agentVoInfo.getSsrcCheck());
        if(ssrcInfo.getPort() <= 0){
            log.error("[点播端口分配异常]，agentCode={},ssrcInfo={}", agentVoInfo.getAgentCode(), ssrcInfo);
            //释放 ssrc
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcInfo.getStream());

            callback.run(InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), "点播端口分配异常", null);
            inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                    InviteErrorCode.ERROR_FOR_RESOURCE_EXHAUSTION.getCode(), "点播端口分配异常", null);
            return;
        }
        // 初始化redis中的invite消息状态
        InviteInfo inviteInfo = new InviteInfo(ObjectUtil.defaultIfNull(JwtUtils.getUserId(), VideoConstant.DEFAULT_DOWNLOAD_USER),agentVoInfo.getAgentCode(), ssrcInfo.getStream(), ssrcInfo,
                mediaServerVo.getSdpIp(), ssrcInfo.getPort(), agentVoInfo.getStreamMode(), VideoStreamType.call_phone,
                InviteSessionStatus.ready);
        inviteStreamManager.updateInviteInfo(inviteInfo);
        //超时任务处理
        String timeOutTaskKey = RandomUtil.randomString(32);
        dynamicTask.startDelay(timeOutTaskKey,sipServer.getVideoProperties().getPlayTimeout(),()->{
            // 执行超时任务时查询是否已经成功，成功了则不执行超时任务，防止超时任务取消失败的情况
            InviteInfo inviteInfoForTimeOut = inviteStreamManager.getInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentVoInfo.getAgentCode());
            if(inviteInfoForTimeOut == null || inviteInfoForTimeOut.getStreamInfo() == null){
                log.info("[点播超时] 收流超时 agentCode: {},，端口：{}, SSRC: {}", agentVoInfo.getAgentCode(), ssrcInfo.getPort(), ssrcInfo.getSsrc());
                // 点播超时回复BYE 同时释放ssrc以及此次点播的资源
                callback.run(InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getCode(), InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamManager.call(VideoStreamType.call_phone,  agentVoInfo.getAgentCode(), null, InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getCode(), InviteErrorCode.ERROR_FOR_STREAM_TIMEOUT.getMsg(), null);
                inviteStreamManager.removeInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentVoInfo.getAgentCode());
                try {
                    sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,ssrcInfo.getStream(),null,null,null,null);
                } catch (InvalidArgumentException | SipException | ParseException e) {
                    log.error("[点播超时]， 发送BYE失败 {}", e.getMessage());
                }finally {
                    ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
                    ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcInfo.getStream());
                    MediaClient.closeRtpServer(mediaServerVo,ssrcInfo.getStream());
                    // 取消订阅消息监听
                    HookKey hookKey = HookKeyFactory.onStreamChanged("rtp", ssrcInfo.getStream(), true, "rtsp", mediaServerVo.getId());
                    mediaHookSubscribe.removeSubscribe(hookKey);
                }
            }
        });
        try {
            SIPRequest rtp = sipCommanderForPlatform.callPhone(sipServer, mediaServerVo, ssrcInfo, agentVoInfo, (media, response) -> {
                log.info("收到订阅消息： " + JSONUtil.toJsonStr(response));
                dynamicTask.stop(timeOutTaskKey);
                OnStreamChangedHookVo vo = (OnStreamChangedHookVo) response;
                StreamInfo streamInfo = new StreamInfo(mediaServerVo, "rtp", vo.getStream(), vo.getTracks(), null, null, agentVoInfo.getAgentCode());
                InviteInfo invite = inviteStreamManager.getInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentVoInfo.getAgentCode());
                if (invite != null) {
                    invite.setStatus(InviteSessionStatus.ok);
                    invite.setStreamInfo(streamInfo);
                    inviteStreamManager.updateInviteInfo(invite);
                }
                callback.run(InviteErrorCode.SUCCESS.getCode(), InviteErrorCode.SUCCESS.getMsg(), streamInfo);
                inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                        InviteErrorCode.SUCCESS.getCode(),
                        InviteErrorCode.SUCCESS.getMsg(),
                        streamInfo);
                //返回成功流信息
                //可获取视频截图
            }, (okEvent) -> {
                inviteOKHandler(sipServer, mediaServerVo, agentVoInfo, ssrcInfo, inviteInfo, okEvent, timeOutTaskKey, callback);
            }, (errEvent) -> {
                log.error("收到订阅错误消息： " + JSONUtil.toJsonStr(errEvent));
                //发送错误后处理
                dynamicTask.stop(timeOutTaskKey);
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(), ssrcInfo.getSsrc());
                ssrcTransactionManager.remove(agentVoInfo.getAgentCode(), ssrcInfo.getStream());
                MediaClient.closeRtpServer(mediaServerVo, ssrcInfo.getStream());
                callback.run(InviteErrorCode.ERROR_FOR_SIGNALLING_ERROR.getCode(),
                        String.format("点播失败， 错误码： %s, %s", errEvent.getStatusCode(), errEvent.getMsg()), null);
                inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                        InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        String.format("点播失败， 错误码： %s, %s", errEvent.getStatusCode(), errEvent.getMsg()), null);

                inviteStreamManager.removeInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentVoInfo.getAgentCode());
            });
            RedisService.getAgentInfoManager().putCallPhone(rtp.getCallId().getCallId(),rtp);
        } catch (InvalidArgumentException | SipException | ParseException e) {
            //发送异常处理
            log.error("[命令发送失败] 点播消息: {}", e.getMessage());
            dynamicTask.stop(timeOutTaskKey);
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcInfo.getStream());
            MediaClient.closeRtpServer(mediaServerVo,ssrcInfo.getStream());
            callback.run(InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getCode(), InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getMsg(), null);
            inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                    InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getCode(),
                    InviteErrorCode.ERROR_FOR_SIP_SENDING_FAILED.getMsg(), null);
            inviteStreamManager.removeInviteInfoByDeviceAndChannel(VideoStreamType.call_phone, agentVoInfo.getAgentCode());
        }
    }

    private void inviteOKHandler(SipServer sipServer, MediaServerVo mediaServerVo, AgentVoInfo agentVoInfo, SSRCInfo ssrcInfo, InviteInfo inviteInfo, EventResult okEvent, String timeOutTaskKey, InviteErrorCallback<Object> callback) {
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();

        inviteInfo.setStatus(InviteSessionStatus.ok);
        //发送成功后操作
        ResponseEvent responseEvent = (ResponseEvent) okEvent.getEvent();
        String contentString = new String(responseEvent.getResponse().getRawContent());
        String ssrcInResponse = SipUtils.getSsrcFromSdp(contentString);
        // 检查是否有y字段
        if (ssrcInResponse != null) {
            // 查询到ssrc不一致且开启了ssrc校验则需要针对处理
            if (ssrcInfo.getSsrc().equals(ssrcInResponse)) {
                if(mediaServerVo.getRtpEnable()==ConstEnum.Flag.NO.getValue() && agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())){
                    log.warn("[Invite 200OK] 单端口收流模式不支持tcp主动模式收流");
                }else if (agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())) {
                    tcpActiveHandler(agentVoInfo,contentString,mediaServerVo,ssrcInfo,timeOutTaskKey,callback);
                    inviteStreamManager.updateInviteInfo(inviteInfo);
                }
                return;
            }
            log.info("[invite 200] 收到, 发现下级自定义了ssrc: {}", ssrcInResponse);
            //单端口模式streamId也有变化，需要重新设置监听
            if(mediaServerVo.getRtpEnable() == ConstEnum.Flag.NO.getValue()){
                SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(agentVoInfo.getAgentCode(), null, inviteInfo.getStream(),null);
                ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),inviteInfo.getStream());
                inviteStreamManager.updateInviteInfoForSSRC(inviteInfo,ssrcInResponse);
                ssrcTransactionManager.put(agentVoInfo.getAgentCode(),paramOne.getCallId(),"rtp",inviteInfo.getStream(),ssrcInResponse,mediaServerVo.getId(),(SIPResponse) responseEvent.getResponse(),paramOne.getType());
                return;
            }
            //当前服务已使用相同的 下级 ssrc 时
            log.info("[Invite 200OK] SSRC修正 {}->{}", ssrcInfo.getSsrc(), ssrcInResponse);
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),ssrcInfo.getSsrc());
            // 更新ssrc
            MediaRestResult result = MediaClient.updateRtpServerSsrc(mediaServerVo, ssrcInfo.getStream(), ssrcInResponse);
            if(result == null || result.getCode() != RespCode.CODE_0.getValue()){
                try {
                    log.warn("[Invite 200OK] 更新ssrc失败，停止点播 {}", agentVoInfo.getAgentCode());
                    sipCommanderForPlatform.streamByeCmd(sipServer,agentVoInfo, ssrcInfo.getStream(), null,null,null,null);
                } catch (InvalidArgumentException | SipException | ParseException e) {
                    log.error("[命令发送失败] 停止点播， 发送BYE: {}", e.getMessage());
                }
                dynamicTask.stop(timeOutTaskKey);
                ssrcTransactionManager.remove(agentVoInfo.getAgentCode(),ssrcInfo.getStream());
                callback.run(InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        "下级自定义了ssrc,重新设置收流信息失败", null);
                inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                        InviteErrorCode.ERROR_FOR_RESET_SSRC.getCode(),
                        "下级自定义了ssrc,重新设置收流信息失败", null);
                return;
            }
            ssrcInfo.setSsrc(ssrcInResponse);
            inviteInfo.setSsrcInfo(ssrcInfo);
            inviteInfo.setStream(ssrcInfo.getStream());
            if (agentVoInfo.getStreamMode().equals(StreamModeType.TCP_ACTIVE.getValue())) {
                tcpActiveHandler(agentVoInfo,contentString,mediaServerVo,ssrcInfo,timeOutTaskKey,callback);
            }
            inviteStreamManager.updateInviteInfo(inviteInfo);
        }
    }

    private void tcpActiveHandler(AgentVoInfo agentVoInfo, String contentString, MediaServerVo mediaServerVo, SSRCInfo ssrcInfo, String timeOutTaskKey, InviteErrorCallback<Object> callback) {
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
            log.info("[TCP主动连接对方] deviceId: {},  连接对方的地址：{}:{}, 收流模式：{}, SSRC: {}, SSRC校验：{}",agentVoInfo.getAgentCode(), sdp.getConnection().getAddress(), port, agentVoInfo.getStreamMode(), ssrcInfo.getSsrc(), agentVoInfo.getSsrcCheck());
            MediaRestResult result = MediaClient.connectRtpServer(mediaServerVo, sdp.getConnection().getAddress(), port, ssrcInfo.getStream());
            log.info("[TCP主动连接对方] 结果： {}", result);
        } catch (SdpException e) {
            log.error("[TCP主动连接对方] AgentCode: {},, 解析200OK的SDP信息失败", agentVoInfo.getAgentCode(), e);
            dynamicTask.stop(timeOutTaskKey);
            MediaClient.closeRtpServer(mediaServerVo,ssrcInfo.getStream());
            // 释放ssrc
            ssrcConfigManager.releaseSsrc(mediaServerVo.getId(), ssrcInfo.getSsrc());
            ssrcTransactionManager.remove(agentVoInfo.getAgentCode(), ssrcInfo.getStream());
            callback.run(InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getCode(),
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getMsg(), null);
            inviteStreamManager.call(VideoStreamType.call_phone, agentVoInfo.getAgentCode(), null,
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getCode(),
                    InviteErrorCode.ERROR_FOR_SDP_PARSING_EXCEPTIONS.getMsg(), null);
        }
    }

    private RestResult<?> stopAudioPushStatus(AgentVoInfo agentVoInfo) {
        SsrcTransaction param = RedisService.getSsrcTransactionManager().getParamOne(agentVoInfo.getAgentCode(), null, null, VideoStreamType.push_web_rtp);
        if(param!= null){
            RedisService.getSsrcTransactionManager().remove(agentVoInfo.getAgentCode(),param.getStream(),VideoStreamType.push_web_rtp);
        }
        dynamicTask.stop(String.format("push_web_rtp:%s",agentVoInfo.getAgentCode()));
        try {
            sipCommander.streamByeCmd(sipServer, agentVoInfo,null,null,VideoStreamType.push_web_rtp,null,null);
        } catch (SipException | InvalidArgumentException | ParseException | SsrcTransactionNotFoundException e) {
            log.error("[命令发送失败] 语音流 发送BYE: {}", e.getMessage());
        }
        return RestResult.result(RespCode.CODE_0.getValue(),null);
    }
}
