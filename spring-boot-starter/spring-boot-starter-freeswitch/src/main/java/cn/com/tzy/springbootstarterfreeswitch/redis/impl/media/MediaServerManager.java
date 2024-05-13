package cn.com.tzy.springbootstarterfreeswitch.redis.impl.media;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
@Component
public class MediaServerManager {

    private String VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX = SipConstant.VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX;

    @Resource
    private VideoProperties videoProperties;

    public void clearRTPServer(MediaServerVo mediaServerVo) {
        RedisUtils.zAdd(VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId(), mediaServerVo.getId(), 0);
    }

    public void clearMediaServerForOnline() {
        String key = VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId();
        RedisUtils.del(key);
    }

    public void resetOnlineServerItem(MediaServerVo mediaServerVo) {
        // 更新缓存
        String key = VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId();
        // 使用zset的分数作为当前并发量， 默认值设置为0
        if (RedisUtils.zScore(key,mediaServerVo.getId()) == null) {  // 不存在则设置默认值 已存在则重置
            RedisUtils.zAdd(key, mediaServerVo.getId(), 0L);
            MediaRestResult result = MediaClient.getMediaList(mediaServerVo, "__defaultVhost__", "rtsp", null, null);
            if(result != null && result.getCode() == RespCode.CODE_0.getValue()){
                List<OnStreamChangedHookVo> onStreamChangedHookVos = JSONUtil.toList(JSONUtil.toJsonStr(result.getData()), OnStreamChangedHookVo.class);
                if(onStreamChangedHookVos != null){
                    RedisUtils.zAdd(key, mediaServerVo.getId(), onStreamChangedHookVos.size());
                }
            }
        }else {
            clearRTPServer(mediaServerVo);
        }
    }

    public void addCount(String mediaServerId) {
        if (mediaServerId == null) {
            return;
        }
        String key = VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId();
        RedisUtils.zIncrScore(key, mediaServerId, 1);
    }

    public void removeCount(String mediaServerId) {
        if (mediaServerId == null) {
            return;
        }
        String key = VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId();
        RedisUtils.zIncrScore(key, mediaServerId, -1);
    }
    /**
     * 获取负载最低的节点
     */
    public String getMediaServerForMinimumLoad() {
        String key = VIDEO_MEDIA_ONLINE_SERVERS_COUNT_PREFIX + videoProperties.getServerId();
        Long size = RedisUtils.zSize(key);
        if (size  == null || size == 0) {
            log.info("获取负载最低的节点时无在线节点");
            return null;
        }
        Set<Object> objects = RedisUtils.zRange(key, 0, -1);
        ArrayList<Object> mediaServerIdList = new ArrayList<>(objects);
        return (String) mediaServerIdList.get(0);
    }
}
