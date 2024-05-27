package cn.com.tzy.springbootstarterfreeswitch.client.media.client;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstarterfreeswitch.client.media.hook.MediaHookServer;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs.AgentInfoManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.MediaServerManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.StreamChangedManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.InviteStreamManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookKeyFactory;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.MediaHookSubscribe;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.MediaService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.*;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class ZlmService {

    private final String zlmKeepaliveKeyPrefix = "zlm-keepalive_";
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private SIPCommander sipCommander;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    @Resource
    private SipServer sipServer;


    //流媒体上线
    public void zlmOnline(ZLMServerConfig zlmServerConfig) {
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerManager mediaServerManager = RedisService.getMediaServerManager();

        MediaServerVo mediaServerVo = mediaServerVoService.findMediaServerId(zlmServerConfig.getGeneralMediaServerId());
        if(mediaServerVo == null){
            log.error("[未注册的zlm] 拒接接入：{}来自{}：{}", zlmServerConfig.getGeneralMediaServerId(), zlmServerConfig.getIp(),zlmServerConfig.getHttpPort() );
            log.error("请检查ZLM的<general.mediaServerId>配置是否与WVP的<media.id>一致");
            return;
        }
        log.info("[ZLM] 正在连接 : {} -> {}:{}", zlmServerConfig.getGeneralMediaServerId(), zlmServerConfig.getIp(), zlmServerConfig.getHttpPort());
        if (mediaServerVo.getHttpPort() == 0) {
            mediaServerVo.setHttpPort(zlmServerConfig.getHttpPort());
        }
        if (mediaServerVo.getHttpSslPort() == 0) {
            mediaServerVo.setHttpSslPort(zlmServerConfig.getHttpSSLport());
        }
        if (mediaServerVo.getRtmpPort() == 0) {
            mediaServerVo.setRtmpPort(zlmServerConfig.getRtmpPort());
        }
        if (mediaServerVo.getRtmpSslPort() == 0) {
            mediaServerVo.setRtmpSslPort(zlmServerConfig.getRtmpSslPort());
        }
        if (mediaServerVo.getRtspPort() == 0) {
            mediaServerVo.setRtspPort(zlmServerConfig.getRtspPort());
        }
        if (mediaServerVo.getRtspSslPort() == 0) {
            mediaServerVo.setRtspSslPort(zlmServerConfig.getRtspSSlport());
        }
        if (mediaServerVo.getRtpProxyPort() == 0) {
            mediaServerVo.setRtpProxyPort(zlmServerConfig.getRtpProxyPort());
        }
        mediaServerVo.setStatus(ConstEnum.Flag.YES.getValue());
        //修改信息
        mediaServerVoService.updateById(mediaServerVo);
        //存储ZLM的ssrc信息
        if(!ssrcConfigManager.hasMediaServerSSRC(mediaServerVo.getId())){
            ssrcConfigManager.initMediaServerSSRC(mediaServerVo.getId(),null);
        }
        //是否自动zlm配置文件
        if(mediaServerVo.getAutoConfig() == ConstEnum.Flag.YES.getValue()){
            //重启时直接关闭全部留
            boolean b = zlmServerConfig.getRestart() == ConstEnum.Flag.YES.getValue();
            cleanStream(mediaServerVo,b);
            MediaClient.zlmConfigAuto(mediaServerVo,b);
            //MediaClient.zlmConfigAuto(mediaServerVo,true);
        }
        //初始化流媒体信息
        mediaServerManager.resetOnlineServerItem(mediaServerVo);
        //定时检测是否在线
        zlmKeepaliveTask(mediaServerVo,false);
        //定时检测流上下线操作并相关处理
        zlmStreamChanged(mediaServerVo);
        //上线操作
        zlmOnline(mediaServerVo);
    }
    /**
     * 心跳任务
     * @param mediaServerVo
     */
    public void zlmKeepaliveTask(MediaServerVo mediaServerVo,boolean isDel){
        MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
        //心跳事件
        HookKey hookKey = HookKeyFactory.onServerKeepalive(mediaServerVo.getId());
        if(isDel){
            mediaHookSubscribe.removeSubscribe(hookKey);
            zlmKeepalive(mediaServerVo, true);
        }else {
            HookKey key = mediaHookSubscribe.getHookKey(hookKey);
            if(key == null){
                mediaHookSubscribe.addSubscribe(hookKey,(server, response)->{
                    zlmKeepalive(server, false);//更新订阅过期时间
                });
            }
        }
    }
    private void zlmKeepalive(MediaServerVo mediaServerVo,boolean isDel){
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerManager mediaServerManager = RedisService.getMediaServerManager();
        MediaHookSubscribe mediaHookSubscribe = MediaService.getMediaHookSubscribe();
        //设置zlm服务上线
        String key = zlmKeepaliveKeyPrefix + mediaServerVo.getId();
        if(isDel){
            dynamicTask.stop(key);
            return;
        }
        //更新流媒体状态
        mediaServerVoService.updateStatus(mediaServerVo.getId(),ConstEnum.Flag.YES.getValue());
        //订阅更新过期时间
        HookKey hookKey = mediaHookSubscribe.getHookKey(HookKeyFactory.onServerKeepalive(mediaServerVo.getId()));
        if(hookKey != null){
            hookKey.updateExpires(null);//更新订阅过期时间
        }

        //保证有一定延迟
        dynamicTask.startCron(key, mediaServerVo.getHookAliveInterval()+ SipConstant.DELAY_TIME,mediaServerVo.getHookAliveInterval()+ SipConstant.DELAY_TIME ,()->{
            //订阅更新过期时间
            HookKey hook = mediaHookSubscribe.getHookKey(HookKeyFactory.onServerKeepalive(mediaServerVo.getId()));
            if(hook != null){
                hook.updateExpires(null);
            }
            MediaRestResult zlmConfig = MediaClient.getZlmConfig(mediaServerVo);
            if(zlmConfig != null && zlmConfig.getCode() == 0){
                log.info("[zlm心跳到期]：{}验证后zlm仍在线，请检查zlm是否可以正常向服务发送心跳", mediaServerVo.getId());
                //在线
                if(!ssrcConfigManager.hasMediaServerSSRC(mediaServerVo.getId())){
                    ssrcConfigManager.initMediaServerSSRC(mediaServerVo.getId(),null);
                }
                mediaServerManager.clearRTPServer(mediaServerVo);
            }else {
                //离线
                log.info("[zlm心跳到期]：{}验证后zlm离线，请检查zlm是否可以正常向服务发送心跳", mediaServerVo.getId());
                mediaServerVoService.updateStatus(mediaServerVo.getId(),ConstEnum.Flag.NO.getValue());
                zlmOffline(mediaServerVo);
            }
        });
    }
    /**
     * zlm上线需操作的
     */
    private void zlmOnline(MediaServerVo mediaServerVo){
        //上线不需要操作什么
    }
    /**
     * zlm下线
     */
    public void zlmOffline(MediaServerVo mediaServerVo){
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        AgentInfoManager agentInfoManager = RedisService.getAgentInfoManager();
        //取消zlm在线认证
        zlmKeepaliveTask(mediaServerVo,true);

        //移除播放等
        List<SendRtp> sendRtpList = sendRtpManager.queryAllSendRTPServer();
        for (SendRtp sendRtp : sendRtpList) {
            if(!sendRtp.getMediaServerId().equals(mediaServerVo.getId())){
                continue;
            }

            AgentVoInfo agentVoInfo = agentInfoManager.get(sendRtp.getAgentKey());
            try {
                sipCommanderForPlatform.streamByeCmd(sipServer, agentVoInfo,sendRtp,null,null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] 国标级联 发送BYE: {}", e.getMessage());
            }
        }
        List<SsrcTransaction> allSsrc = ssrcTransactionManager.getAllSsrc();
        for (SsrcTransaction ssrcTransaction : allSsrc) {
            if(!ssrcTransaction.getMediaServerId().equals(mediaServerVo.getId())){
                continue;
            }
            AgentVoInfo agentVoInfo = agentInfoManager.get(ssrcTransaction.getAgentKey());
            if(agentVoInfo == null){
                continue;
            }
            try {
                sipCommander.streamByeCmd(sipServer, agentVoInfo,ssrcTransaction.getStream(),null,null,null,null);
            }catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e){
                log.error("[无人观看]回放， 发送BYE失败 {}", e.getMessage());
            }
        }
    }
    //定时检测流上下线操作并相关处理
    public void zlmStreamChanged(MediaServerVo mediaServerVo){
        dynamicTask.startCron(StreamChangedManager.VIDEO_MEDIA_STREAM_CHANGED_PREFIX,60,()->{
            cleanStream(mediaServerVo,false);
        });
    }

    private  void cleanStream(MediaServerVo mediaServerVo,boolean delAll){
        StreamChangedManager streamChangedManager = RedisService.getStreamChangedManager();
        MediaServerVoService mediaServerService = SipService.getMediaServerService();
        MediaHookServer mediaHookServer = SpringUtil.getBean(MediaHookServer.class);
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        AgentVoService agentVoService = FsService.getAgentService();

        List<OnStreamChangedHookVo> mediaServerAll = streamChangedManager.getMediaServerAll(mediaServerVo.getId());
        if(mediaServerAll != null && !mediaServerAll.isEmpty()){
            List<OnStreamChangedHookVo> delete = new ArrayList<>();
            for (OnStreamChangedHookVo vo : mediaServerAll) {
                MediaServerVo mediaServer = mediaServerService.findMediaServerId(vo.getMediaServerId());
                if(mediaServer == null){
                    delete.add(vo);
                    continue;
                }
                OnStreamChangedResult result = MediaClient.getMediaInfo(mediaServerVo, null, vo.getSchema(), vo.getApp(), vo.getStream());
                if(delAll || result == null || result.getCode() != RespCode.CODE_0.getValue() || result.getTotalReaderCount() <= 0){
                    delete.add(vo);
                    continue;
                }
            }
            for (OnStreamChangedHookVo vo : delete) {
                OnStreamNoneReaderHookVo build = OnStreamNoneReaderHookVo.builder()
                        .app(vo.getApp())
                        .stream(vo.getStream())
                        .schema(vo.getSchema())
                        .mediaServerId(vo.getMediaServerId())
                        .build();
                log.info("定时任务检测到无人观看流，自动关闭：{}",build);
                mediaHookServer.onStreamNoneReader(build);
                streamChangedManager.remove(vo);
                //只有重启 zlm 时 delAll = true
                // onStreamNoneReader 调用后 streamByeCmd 发送注销 ，这时zlm重启， 不触发hook onStreamChanged，手动删除及修改状态
                if(delAll){
                    InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByStream(null, vo.getStream());
                    if(inviteInfo != null){
                        agentVoService.stopPlay(inviteInfo.getAgentKey());
                        inviteStreamManager.removeInviteInfo(inviteInfo);
                    }
                }
            }

        }
    }
}
