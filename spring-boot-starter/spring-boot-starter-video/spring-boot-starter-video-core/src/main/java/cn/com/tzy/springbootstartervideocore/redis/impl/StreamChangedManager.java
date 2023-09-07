package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideobasic.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 流注册注销时缓存
 */
public class StreamChangedManager {
    @Resource
    private VideoProperties videoProperties;
    public static final String VIDEO_MEDIA_STREAM_CHANGED_PREFIX = VideoConstant.VIDEO_MEDIA_STREAM_CHANGED_PREFIX;

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
