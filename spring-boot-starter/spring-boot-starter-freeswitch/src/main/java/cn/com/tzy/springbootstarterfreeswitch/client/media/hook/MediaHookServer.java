package cn.com.tzy.springbootstarterfreeswitch.client.media.hook;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.ZlmService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.enums.media.HookType;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs.AgentInfoManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.MediaServerManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.StreamChangedManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.result.DeferredResultHolder;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.InviteStreamManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.service.media.TokenService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.*;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.FsRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 流媒体服务回调事件
 */
@Log4j2
@Component
public class MediaHookServer {

    @Resource
    private SipServer sipServer;
    @Resource
    private MediaHookSubscribe mediaHookSubscribe;
    @Resource
    private VideoProperties videoProperties;
    @Resource
    private SIPCommander sipCommander;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;

    /**
     * 流媒体心跳回调
     * @param hookVo
     */
    public NotNullMap onServerKeepalive(HookVo hookVo){
        log.info("[ZLM HOOK] 收到zlm心跳：" + hookVo.getMediaServerId());
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        String mediaServerId = hookVo.getMediaServerId();
        MediaServerVo mediaServerVo = mediaServerVoService.findMediaServerId(mediaServerId);
        ThreadUtil.execute(()->{
            if(mediaServerVo != null){
                mediaHookSubscribe.sendNotify(MediaHookVo.builder().type(HookType.on_server_keepalive).onAll(ConstEnum.Flag.NO.getValue()).mediaServerVo(mediaServerVo).hookVo(hookVo).build());
            }
        });
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }

    /**
     * 播放器鉴权事件，rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件。
     * @param hookVo
     */
    public NotNullMap onPlay(OnPlayHookVo hookVo){
        log.debug("[ZLM HOOK] 播放鉴权：{}->{}" + hookVo.getMediaServerId(), hookVo);
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        TokenService tokenService = MediaService.getTokenService();

        String mediaServerId = hookVo.getMediaServerId();
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(mediaServerId);
        if (hookVo.getParams() == null) {
            log.info("播放鉴权失败： 缺少不要参数：sign=md5(user表的pushKey)");
            return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
        }
        Map<String, String> paramMap = HttpUtil.decodeParamMap(hookVo.getParams(), CharsetUtil.CHARSET_UTF_8);
        String token = paramMap.get("token");
        if (token == null) {
            log.info("播放鉴权失败： 缺少必要参数：token");
            return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
        }
        boolean authentication = tokenService.authentication(token);
        if(! authentication){
            log.info("播放鉴权失败： 用户 无权限:  token={}", token);
            return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
        }
        ThreadUtil.execute(()->{
            if(mediaServerVo != null){
                mediaHookSubscribe.sendNotify(MediaHookVo.builder().type(HookType.on_play).onAll(ConstEnum.Flag.NO.getValue()).mediaServerVo(mediaServerVo).hookVo(hookVo).build());
            }
        });
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }

    /**
     * rtsp/rtmp/rtp推流鉴权事件。
     * @param hookVo
     */
    public NotNullMap onPublish(OnPublishHookVo hookVo){
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        TokenService tokenService = MediaService.getTokenService();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();

        String mediaServerId = hookVo.getMediaServerId();
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(mediaServerId);
        NotNullMap map = new NotNullMap();
        map.putInteger("code",0);
        map.putString("msg","success");
        map.putInteger("mp4_max_second",0);
        boolean enableAudio = false;
        boolean enableMp4 = false;
        if(!"rtp".equals(hookVo.getApp())){
            //是否开启鉴权
            if(videoProperties.getPushAuthority()){
                if (hookVo.getParams() == null) {
                    log.info("推流鉴权失败： 缺少不要参数：sign=md5(user表的pushKey)");
                    return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
                }
                Map<String, String> paramMap = HttpUtil.decodeParamMap(hookVo.getParams(), CharsetUtil.CHARSET_UTF_8);
                String token = paramMap.get("token");
                if (token == null) {
                    log.info("推流鉴权失败： 缺少必要参数：token");
                    return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
                }
                boolean authentication = tokenService.authentication(token);
                if(! authentication){
                    log.info("推流鉴权失败： 用户 无权限:  token={}", token);
                    return  new NotNullMap(){{putInteger("code",401);putString("msg","Unauthorized");}};
                }
            }
        }else {
            enableMp4 = videoProperties.getRecordSip();
            // 替换流地址
            if(mediaServerVo.getRtpEnable() == ConstEnum.Flag.NO.getValue()){
                String ssrc = String.format("%010d", Long.parseLong(hookVo.getStream(), 16));;
                InviteInfo inviteInfo = inviteStreamManager.getInviteInfoBySSRC(ssrc);
                if (inviteInfo != null) {
                    log.info("[ZLM HOOK]推流鉴权 stream: {} 替换为 {}", hookVo.getStream(), inviteInfo.getStream());
                    map.put("stream_replace",inviteInfo.getStream());
                    hookVo.setStream(inviteInfo.getStream());
                }
            }
        }
        SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(null, null,hookVo.getStream(),null);
        if(ssrcTransaction != null){
            enableAudio = true;
            enableMp4 = false;

            // 如果是录像下载就设置视频间隔十秒
            if(ssrcTransaction.getType() == VideoStreamType.download){
                enableMp4 = true;
                map.putInteger("mp4_max_second",10);
            }
        }
        //发送推流事件处理
        ThreadUtil.execute(()->{
            if(mediaServerVo != null){
                mediaHookSubscribe.sendNotify(MediaHookVo.builder().type(HookType.on_publish).onAll(ConstEnum.Flag.NO.getValue()).mediaServerVo(mediaServerVo).hookVo(hookVo).build());
            }
        });
        map.put("enable_audio",enableAudio);
        map.put("enable_mp4",enableMp4);
        log.info("[ZLM HOOK]推流鉴权 响应：{}->{}->>>>{}", hookVo.getMediaServerId(), JSONUtil.toJsonStr(hookVo), JSONUtil.toJsonStr(map));
        return map;
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感。
     * @param hookVo
     */
    public NotNullMap onStreamChanged(OnStreamChangedHookVo hookVo){
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        MediaServerManager mediaServerManager = RedisService.getMediaServerManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        StreamChangedManager streamChangedManager = RedisService.getStreamChangedManager();
        DeferredResultHolder deferredResultHolder = SpringUtil.getBean(DeferredResultHolder.class);
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        AgentVoService agentVoService = FsService.getAgentService();
        if(hookVo.isRegist()){
            log.info("[ZLM HOOK] 流注册, {}->{}->{}/{}", hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());
        }else {
            log.info("[ZLM HOOK] 流注销, {}->{}->{}/{}", hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());
        }
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(hookVo.getMediaServerId());
        if (mediaServerVo == null) {
            log.info("[ZLM HOOK] 流变化未找到ZLM, {}", hookVo.getMediaServerId());
            return new NotNullMap(){{putInteger("code",1);putString("msg","zlm is null");}};
        }
        ThreadUtil.execute(()->{
            //维护数据
            if("rtsp".equals(hookVo.getSchema())){
                if(hookVo.isRegist()){
                    mediaServerManager.addCount(hookVo.getMediaServerId());
                    streamChangedManager.put(hookVo);
                }else {
                    mediaServerManager.removeCount(hookVo.getMediaServerId());
                    streamChangedManager.remove(hookVo);
                }
                mediaHookSubscribe.sendNotify(MediaHookVo.builder().type(HookType.on_stream_changed).onAll(ConstEnum.Flag.NO.getValue()).mediaServerVo(mediaServerVo).hookVo(hookVo).build());
                if("rtp".equals(hookVo.getApp())){
                    InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByStream(null, hookVo.getStream());
                    if(inviteInfo != null && (inviteInfo.getType() == VideoStreamType.call_phone || inviteInfo.getType() == VideoStreamType.playback)){
                        if(hookVo.isRegist()){
                            agentVoService.startPlay(inviteInfo.getAgentCode(),inviteInfo.getStream());
                        }else {
                            //设备播放流
                            agentVoService.stopPlay(inviteInfo.getAgentCode());
                            inviteStreamManager.removeInviteInfo(inviteInfo);
                            String key = String.format("%s%s",DeferredResultHolder.CALLBACK_CMD_STOP,inviteInfo.getAgentCode());
                            deferredResultHolder.invokeAllResult(key, RestResult.result(RespCode.CODE_0.getValue(),"停止点播成功"));
                        }
                    }
                }else if("push_web_rtp".equals(hookVo.getApp())){
                    log.info("[ZLM HOOK] 视频 | 语音推流, {}->{}->{}/{}", hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());
                    SsrcTransaction paramOne = ssrcTransactionManager.getParamOne(hookVo.getStream(), null, null, VideoStreamType.push_web_rtp);
                    if(hookVo.isRegist()){
                        if(paramOne !=null){
                            paramOne.setOnPush(true);
                            ssrcTransactionManager.put(paramOne);
                        }
                        FsService.getSendAgentMessage().sendMessage(AgentCommon.SOCKET_AGENT,AgentCommon.AGENT_OUT_PUSH_PATH_OK,paramOne.getAgentCode(),RestResult.result(RespCode.CODE_0.getValue(),"推流接收成功",null));
                    }else {
                        ssrcTransactionManager.remove(paramOne.getAgentCode(),paramOne.getStream());
                        FsService.getSendAgentMessage().sendMessage(AgentCommon.SOCKET_AGENT,AgentCommon.AGENT_OUT_PUSH_PATH_LOGOUT,paramOne.getAgentCode(),RestResult.result(RespCode.CODE_0.getValue(),"推流关闭成功",null));
                    }
                }else {
                    log.error("[ZLM HOOK] 未知流，请查询接口, {}->{}->{}/{}", hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());
                }
                //流注销时触发
                if(!hookVo.isRegist()){
                    List<SendRtp> sendRtpList = sendRtpManager.querySendRTPServerByStream(hookVo.getStream());
                    for (SendRtp sendRtp : sendRtpList) {
                        // 设备编号 或 上级平台
                        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(sendRtp.getAgentCode());
                        if(agentVoInfo == null){
                            log.warn("[ZLM HOOK 未获取客服编号：{} 信息]",sendRtp.getAgentCode());
                            continue;
                        }
                        try {
                            if(StringUtils.isEmpty(agentVoInfo.getRemoteAddress())){
                                sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtp,null,null);
                            }else {
                                sipCommander.streamByeCmd(sipServer, agentVoInfo,sendRtp.getStreamId(),sendRtp.getCallId(),null,null,null);
                            }
                            sendRtpManager.deleteSendRTPServer(sendRtp.getAgentCode(),sendRtp.getStreamId(),sendRtp.getCallId());
                        }catch (Exception e){
                            log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
                        }
                    }
                }
            }
        });
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。
     *
     */
    public DeferredResult<NotNullMap> onStreamNoneReader(OnStreamNoneReaderHookVo hookVo){
        log.info("[ZLM HOOK]流无人观看：{}->{}->{}/{}" ,hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        DeferredResultHolder deferredResultHolder = SpringUtil.getBean(DeferredResultHolder.class);
        AgentInfoManager agentInfoManager = RedisService.getAgentInfoManager();
        FsRestResult<NotNullMap> deferredResult = new FsRestResult<>(videoProperties.getPlayTimeout()*1000L,()-> new NotNullMap(){{putInteger("code",0);putString("msg","success");}});
        String key = String.format("%s%s",DeferredResultHolder.CALLBACK_STREAM_NONE_READER,hookVo.getStream());
        String uuid = RandomUtil.randomString(32);
        deferredResultHolder.put(key,uuid,deferredResult);

        NotNullMap map = new NotNullMap();
        map.putInteger("code",0);
        map.putString("msg","success");
        if("rtp".equals(hookVo.getApp())){
            map.put("close",videoProperties.getStreamOnDemand());
            //获取国标流 ， 点播/录像回放/录像下载
            //点播
            InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByStream(null, hookVo.getStream());
            log.info("无人观看流时 {}，{}",hookVo.getStream(), ObjectUtils.isEmpty(inviteInfo)?"未发现 inviteInfo":"发现 inviteInfo");
            if(inviteInfo != null){
                // 录像下载
                if (inviteInfo.getType() == VideoStreamType.download) {
                    map.put("close", false);
                    deferredResultHolder.invokeResult(key,uuid,map);
                    return deferredResult;
                }
                //收到无人观看说明流也没有在往上级推送
                if(sendRtpManager.isChannelSendingRTP(inviteInfo.getAgentCode())){
                    List<SendRtp> sendRtpList = sendRtpManager.querySendRTPServerByChnnelId(inviteInfo.getAgentCode());
                    for (SendRtp sendRtp : sendRtpList) {
                        AgentVoInfo agentVoInfo = agentInfoManager.get(sendRtp.getAgentCode());
                        if(agentVoInfo != null && StringUtils.isBlank(agentVoInfo.getRemoteAddress())){
                            try {
                                sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtp,null,null);
                            }catch (InvalidArgumentException | ParseException | SipException e){
                                log.error("[无人观看]点播， 发送 国标级联 BYE失败 {}", e.getMessage());
                            }
                        }
                    }
                }
                AgentVoInfo agentVoInfo = agentInfoManager.get(inviteInfo.getAgentCode());
                if(agentVoInfo != null){
                    try {
                        sipCommander.streamByeCmd(sipServer, agentVoInfo,inviteInfo.getStream(),null,null,(ok)->{
                            deferredResultHolder.invokeResult(key,uuid,map);
                        },(error)->{
                            log.error("[无人观看]点播， BYE异常 {}",error.getMsg());
                            deferredResultHolder.invokeResult(key,uuid,map);
                        });
                    }catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e){
                        log.error("[无人观看]点播， 发送BYE失败 {}", e.getMessage());
                    }
                    return deferredResult;
                }else {
                    deferredResultHolder.invokeResult(key,uuid,map);
                    return deferredResult;
                }
            }else {
                deferredResultHolder.invokeResult(key,uuid,map);
                return deferredResult;
            }
        }else {
            map.put("close", true);
            deferredResultHolder.invokeResult(key,uuid,map);
            return deferredResult;
            //推流代理
            //推流暂不处理
        }
    }

    /**
     * 流未找到事件，不处理
     */
    public DeferredResult<NotNullMap> onStreamNotFound(OnStreamNotFoundHookVo hookVo){
        log.info("[ZLM HOOK] 流未找到：{}->{}->{}/{}", hookVo.getMediaServerId(), hookVo.getSchema(), hookVo.getApp(), hookVo.getStream());

        FsRestResult<NotNullMap> deferredResult = new FsRestResult<>(videoProperties.getPlayTimeout()*1000L,()->{return new NotNullMap(){{putInteger("code",404);putString("msg","资源未找到");}};});
        deferredResult.setResult(new NotNullMap(){{putInteger("code",0);putString("msg","success");}});
        return deferredResult;
    }

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感。
     * @param ip 流媒体服务 回调ip
     * @param hookVo
     */
    public NotNullMap onServerStarted(String ip,JSONObject hookVo){
        hookVo.putOpt("ip",ip);
        ZLMServerConfig zlmServerConfig = JSONUtil.toBean(hookVo, ZLMServerConfig.class);
        MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
        ZlmService zlmService = SpringUtil.getBean(ZlmService.class);
        ThreadUtil.execute(()->{
            zlmServerConfig.setRestart(ConstEnum.Flag.NO.getValue());
            mediaHookSubscribe.sendNotify(MediaHookVo.builder().type(HookType.on_server_started).onAll(ConstEnum.Flag.YES.getValue()).mediaServerVo(null).hookVo(zlmServerConfig).build());
            zlmService.zlmOnline(zlmServerConfig);
        });
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }

    /**
     * 发送rtp(startSendRtp)被动关闭时回调
     */
    public NotNullMap onSendRtpStopped(OnSendRtpStoppedHookVo hookVo){
        log.info("[ZLM HOOK] rtp发送关闭：{}->{}/{}", hookVo.getMediaServerId(), hookVo.getApp(), hookVo.getStream());
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        AgentInfoManager agentInfoManager = RedisService.getAgentInfoManager();
        if("rtp".equals(hookVo.getApp())){
            ThreadUtil.execute(()->{
                List<SendRtp> sendRtpList = sendRtpManager.querySendRTPServerByStream(hookVo.getStream());
                for (SendRtp sendRtp : sendRtpList) {
                    ssrcConfigManager.releaseSsrc(sendRtp.getMediaServerId(),sendRtp.getSsrc());
                    // 设备编号 或 上级平台
                    String platformId = sendRtp.getAgentCode();
                    AgentVoInfo agentVoInfo = agentInfoManager.get(platformId);
                    if(agentVoInfo == null){
                        log.error("[命令发送失败] 国标级联 发送BYE: {}", platformId);
                       continue;
                    }
                    try {
                        if(StringUtils.isEmpty(agentVoInfo.getRemoteAddress())){
                            sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtp,null,null);
                        }else {
                            sipCommander.streamByeCmd(sipServer, agentVoInfo,sendRtp.getStreamId(),sendRtp.getCallId(),null,null,null);
                        }
                    }catch (Exception e){
                        log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
                    }
                    sendRtpManager.deleteSendRTPServer(sendRtp.getAgentCode(),sendRtp.getStreamId(),sendRtp.getCallId());
                }
            });
        }
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }

    /**
     * rtpServer收流超时
     */
    public NotNullMap onRtpServerTimeout(OnRtpServerTimeoutHookVo hookVo){
        log.info("[ZLM HOOK] rtpServer收流超时：{}->{}({})", hookVo.getMediaServerId(), hookVo.getStream_id(), hookVo.getSsrc());
        ThreadUtil.execute(()->{
            List<HookEvent> subscribes = mediaHookSubscribe.getSubscribes(HookType.on_rtp_server_timeout);
            if(subscribes != null && !subscribes.isEmpty()){
                for (HookEvent subscribe : subscribes) {
                    subscribe.response(null,hookVo);
                }
            }
        });
        return new NotNullMap(){{putInteger("code",0);putString("msg","success");}};
    }
}
