package cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.InviteSessionStatus;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.callback.InviteErrorCallback;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.InviteInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.StreamInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Component
public class InviteStreamManager {

    private String INVITE_PREFIX = SipConstant.INVITE_PREFIX;

    private String INVITE_DOWNLOAD_USER_PREFIX = SipConstant.INVITE_DOWNLOAD_USER_PREFIX;

    private final Map<String, List<InviteErrorCallback<Object>>> inviteErrorCallbackMap = new ConcurrentHashMap<>();

    public InviteInfo updateInviteInfoForSSRC(InviteInfo inviteInfo, String ssrc){
        InviteInfo info = getInviteInfo(inviteInfo.getType(), inviteInfo.getAgentKey(), inviteInfo.getAudioSsrcInfo().getStream(),null);
        if(info == null || info.getAudioSsrcInfo() == null){
            return null;
        }

        removeInviteInfo(info);
        info.getAudioSsrcInfo().setSsrc(ssrc);
        RedisUtils.set(getKey(info.getType(),info.getAgentKey(),info.getAudioSsrcInfo().getStream(),info.getAudioSsrcInfo().getSsrc()), info);
        return info;
    }


    public void updateInviteInfo(InviteInfo inviteInfo) {
        if (inviteInfo == null || (inviteInfo.getAgentKey() == null)) {
            log.warn("[更新Invite信息]，参数不全： {}", JSONUtil.toJsonStr(inviteInfo));
            return;
        }
        InviteInfo inviteInfoForUpdate = null;

        if (InviteSessionStatus.ready == inviteInfo.getStatus()) {
            if (inviteInfo.getAgentKey() == null
                    || inviteInfo.getType() == null
                    || inviteInfo.getAudioSsrcInfo().getStream() == null
            ) {
                return;
            }
            inviteInfoForUpdate = inviteInfo;
        } else {
            InviteInfo inviteInfoInRedis = getInviteInfo(inviteInfo.getType(), inviteInfo.getAgentKey(), inviteInfo.getAudioSsrcInfo().getStream(),null);
            if (inviteInfoInRedis == null) {
                log.warn("[更新Invite信息]，未从缓存中读取到Invite信息： deviceId: {},  stream: {}", inviteInfo.getAgentKey(),  inviteInfo.getAudioSsrcInfo().getStream());
                return;
            }
            if (inviteInfo.getStreamInfo() != null) {
                inviteInfoInRedis.setStreamInfo(inviteInfo.getStreamInfo());
            }
            if (inviteInfo.getAudioSsrcInfo() != null) {
                inviteInfoInRedis.setAudioSsrcInfo(inviteInfo.getAudioSsrcInfo());
            }
            if (inviteInfo.getVideoSsrcInfo() != null) {
                inviteInfoInRedis.setVideoSsrcInfo(inviteInfo.getVideoSsrcInfo());
            }
            if (inviteInfo.getStreamMode() != null) {
                inviteInfoInRedis.setStreamMode(inviteInfo.getStreamMode());
            }
            if (inviteInfo.getReceiveIp() != null) {
                inviteInfoInRedis.setReceiveIp(inviteInfo.getReceiveIp());
            }
            if (inviteInfo.getStatus() != null) {
                inviteInfoInRedis.setStatus(inviteInfo.getStatus());
            }
            inviteInfoForUpdate = inviteInfoInRedis;
        }
        //下载操作时关联用户与当前key的
        if(inviteInfoForUpdate.getType() == VideoStreamType.DOWNLOAD && InviteSessionStatus.ok == inviteInfoForUpdate.getStatus()){
            String userKey = String.format("%s:%s",INVITE_DOWNLOAD_USER_PREFIX,inviteInfoForUpdate.getUserId());
            RedisUtils.sSet(userKey,inviteInfoForUpdate.getAudioSsrcInfo().getStream());
        }
        RedisUtils.set(getKey(inviteInfoForUpdate.getType(),inviteInfoForUpdate.getAgentKey() ,inviteInfoForUpdate.getAudioSsrcInfo().getStream(),inviteInfoForUpdate.getAudioSsrcInfo().getSsrc()), inviteInfoForUpdate);
        RedisService.getRecordMp4Manager().put(inviteInfoForUpdate.getAudioSsrcInfo().getStream(),inviteInfoForUpdate);
    }

    public InviteInfo getInviteInfo(VideoStreamType type, String agentKey,  String stream, String ssrc) {
        List<String> scan = RedisUtils.keys(getKey(type,agentKey,stream,ssrc));
        if (scan.size() > 0) {
            return (InviteInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    /**
     * 获取当前用户下所有下载视频
     * @param userId
     * @return
     */
    public List<NotNullMap> getUserDownloadInviteInfoList(Long userId){
        List<NotNullMap> inviteInfoList= new ArrayList<>();
        String userKey = String.format("%s:%s",INVITE_DOWNLOAD_USER_PREFIX,userId);
        Set<Object> keyList = RedisUtils.sGet(userKey);
        if(keyList == null || keyList.isEmpty()){
            return new ArrayList<>();
        }
        for (Object o : keyList) {
            Object obj = RedisUtils.get(o.toString());
            if(ObjectUtils.isNotEmpty(obj)){
                InviteInfo inviteInfo = (InviteInfo) obj;
                StreamInfo streamInfo = inviteInfo.getStreamInfo();
                if(streamInfo != null){
                    inviteInfoList.add(new NotNullMap(){{
                        putString("id",o.toString());
                        putString("app",streamInfo.getApp());
                        putString("stream",streamInfo.getStream());
                        putString("agentCode",streamInfo.getAgentCode());
                        putString("mediaServerId",streamInfo.getMediaServerId());
                        putString("startTime",streamInfo.getStartTime());
                        putString("endTime",streamInfo.getEndTime());
                        putDouble("progress",streamInfo.getProgress());
                    }});
                }
            }
        }
        return inviteInfoList;
    }

    /**
     * 清除用户下载录像
     * @param userId
     * @param key
     */
    public void delUserDownloadInviteInfoList(Long userId,String key){
        String userKey = String.format("%s:%s",INVITE_DOWNLOAD_USER_PREFIX,userId);
        if(RedisUtils.sHasKey(userKey,key)){
            InviteInfo inviteInfo = (InviteInfo) RedisUtils.get(key);
            if(inviteInfo != null && inviteInfo.getStatus() == InviteSessionStatus.ok && inviteInfo.getStreamInfo() != null){
                StreamInfo streamInfo = inviteInfo.getStreamInfo();
                MediaServerVo mediaServerVo = SipService.getMediaServerService().findOnLineMediaServerId(streamInfo.getMediaServerId());
                if(mediaServerVo == null){
                    log.error("流媒体[{}]未上线，无法清除用户下载录像",streamInfo.getMediaServerId());
                    return;
                }
                MediaClient.deleteRecordDirectory(mediaServerVo,"__defaultVhost__",streamInfo.getApp(),streamInfo.getStream(),null);
                RedisService.getRecordMp4Manager().del(inviteInfo.getAudioSsrcInfo().getStream());
                RedisUtils.del(key);
            }
            RedisUtils.setRemove(userKey,key);
        }
    }

    public InviteInfo getInviteInfoBySSRC(String ssrc) {
        return getInviteInfo(null, null, null,ssrc);
    }

    public InviteInfo getInviteInfoByDeviceAndChannel(VideoStreamType type, String agentKey) {
        return getInviteInfo(type, agentKey, null,null);
    }

    /**
     * 流规则Id目前规则
     * 实时播放 流Id规则 国标编号+通道编号
     * 历史播放 流Id规则 国标编号+通道编号+开始时间戳+结束时间戳
     * 下载 流Id规则 ssrc 的16进制 取前8位
     */
    public InviteInfo getInviteInfoByStream(VideoStreamType type, String stream) {
        return getInviteInfo(type, null, stream,null);
    }

    public void removeInviteInfo(VideoStreamType type, String agentKey,  String stream) {
        List<String> scan = RedisUtils.keys(getKey(type,agentKey,stream,null));
        if (scan.size() > 0) {
            MediaServerVoService mediaServerService = SipService.getMediaServerService();
            for (String key : scan) {
                InviteInfo inviteInfo = (InviteInfo) RedisUtils.get(key);;
                boolean del = false;
                if (inviteInfo != null) {
                    del = true;
                    if(inviteInfo.getStreamInfo() != null){
                        MediaServerVo mediaServerVo = mediaServerService.findOnLineMediaServerId(inviteInfo.getStreamInfo().getMediaServerId());
                        if(mediaServerVo != null){
                            OnStreamChangedResult result = MediaClient.getMediaInfo(mediaServerVo, null, "rtsp", inviteInfo.getStreamInfo().getApp(), inviteInfo.getStreamInfo().getStream());
                            if(result != null && result.getCode() == RespCode.CODE_0.getValue()){
                                del = false;
                            }
                        }
                    }
                }
                if(del){
                    RedisUtils.del(key);
                    RedisService.getRecordMp4Manager().del(inviteInfo.getAudioSsrcInfo().getStream());
                    String userKey = String.format("%s:%s",INVITE_DOWNLOAD_USER_PREFIX,inviteInfo.getUserId());
                    RedisUtils.setRemove(userKey,key);
                    inviteErrorCallbackMap.remove(buildKey(type, agentKey, inviteInfo.getAudioSsrcInfo().getStream()));
                }
            }
        }
    }

    public void removeInviteInfoByDeviceAndChannel(VideoStreamType inviteSessionType, String agentKey) {
        removeInviteInfo(inviteSessionType, agentKey, null);
    }

    public void removeInviteInfo(InviteInfo inviteInfo) {
        removeInviteInfo(inviteInfo.getType(), inviteInfo.getAgentKey(), inviteInfo.getAudioSsrcInfo().getStream());
    }

    public void once(VideoStreamType type, String agentCode, String stream, InviteErrorCallback<Object> callback) {
        String key = buildKey(type, agentCode, stream);
        List<InviteErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            callbacks = new CopyOnWriteArrayList<>();
            inviteErrorCallbackMap.put(key, callbacks);
        }
        callbacks.add(callback);
    }

    public void call(VideoStreamType type, String agentCode, String stream, int code, String msg, Object data) {
        String key = buildKey(type, agentCode, stream);
        List<InviteErrorCallback<Object>> callbacks = inviteErrorCallbackMap.get(key);
        if (callbacks == null) {
            return;
        }
        for (InviteErrorCallback<Object> callback : callbacks) {
            callback.run(code, msg, data);
        }
        inviteErrorCallbackMap.remove(key);
    }

    private String buildKey(VideoStreamType type, String agentCode,  String stream) {
        String key = type + ":" +  agentCode;
        // 如果ssrc未null那么可以实现一个通道只能一次操作，ssrc不为null则可以支持一个通道多次invite
        if (stream != null) {
            key += (":" + stream);
        }
        return key;
    }

    public void clearInviteInfo(String agentKey) {
        removeInviteInfo(VideoStreamType.CALL_VIDEO_PHONE, agentKey, null);
        removeInviteInfo(VideoStreamType.CALL_AUDIO_PHONE, agentKey, null);
    }

    public String getKey(VideoStreamType type,String agentKey,String stream,String ssrc){
        String typeName ="*";
        if(type != null){
            typeName = String.valueOf(type.ordinal());
        }
        if(StringUtils.isBlank(agentKey)){
            agentKey = "*";
        }
        if(StringUtils.isBlank(stream)){
            stream = "*";
        }
        if(StringUtils.isBlank(ssrc)){
            ssrc = "*";
        }
        return String.format("%s:%s:%s:%s:%s",INVITE_PREFIX,typeName,agentKey,stream,ssrc);
    }
}
