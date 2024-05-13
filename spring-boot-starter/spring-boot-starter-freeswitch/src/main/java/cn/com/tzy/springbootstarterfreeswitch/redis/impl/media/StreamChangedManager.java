package cn.com.tzy.springbootstarterfreeswitch.redis.impl.media;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.VideoProperties;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 流注册注销时缓存
 */
@Component
public class StreamChangedManager {
    @Resource
    private VideoProperties videoProperties;
    public static final String VIDEO_MEDIA_STREAM_CHANGED_PREFIX = SipConstant.VIDEO_MEDIA_STREAM_CHANGED_PREFIX;

    public void put(OnStreamChangedHookVo vo) {
        String key = String.format("%s%s:%s:%s:%s:%s", VIDEO_MEDIA_STREAM_CHANGED_PREFIX,videoProperties.getServerId(), vo.getMediaServerId(), vo.getApp(), vo.getStream(),vo.getSchema());
        RedisUtils.set(key, vo);
    }

    public void remove(OnStreamChangedHookVo vo) {
        String key = String.format("%s%s:%s:%s:%s:%s", VIDEO_MEDIA_STREAM_CHANGED_PREFIX,videoProperties.getServerId(), vo.getMediaServerId(), vo.getApp(), vo.getStream(),vo.getSchema());
        RedisUtils.del(key);
    }

    public List<OnStreamChangedHookVo> getMediaServerAll(String mediaServerId) {
        String key = String.format("%s%s:%s:%s", VIDEO_MEDIA_STREAM_CHANGED_PREFIX,videoProperties.getServerId(), mediaServerId,"*");
        List<String> scanResult = RedisUtils.keys(key);
        if (scanResult.size() == 0) {
            return null;
        }
        List<OnStreamChangedHookVo> result = new ArrayList<>();
        for (String keyObj : scanResult) {
            result.add((OnStreamChangedHookVo)RedisUtils.get( keyObj));
        }
        return result;
    }

}
