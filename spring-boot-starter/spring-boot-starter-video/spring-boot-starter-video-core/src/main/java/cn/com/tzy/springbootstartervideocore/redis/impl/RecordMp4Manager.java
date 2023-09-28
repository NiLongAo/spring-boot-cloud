package cn.com.tzy.springbootstartervideocore.redis.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideobasic.common.VideoConstant;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class RecordMp4Manager {

    @Resource
    private VideoProperties videoProperties;

    private String VIDEO_RECORD_MP4_INFO = VideoConstant.VIDEO_RECORD_MP4_INFO;


    public Map<String,Object> get(String streamId){
       return (Map<String,Object>) RedisUtils.get(getKey(streamId));
    }

    public void put(String streamId,InviteInfo inviteInfo){
        if(inviteInfo == null || inviteInfo.getStreamInfo() == null){
            return;
        }
        String key = getKey(streamId);
        Map<Object, Object> data = new HashMap<>();
        data.put("type",inviteInfo.getType().ordinal());
        data.put("streamInfo", JSONUtil.toJsonStr(inviteInfo.getStreamInfo()));
        RedisUtils.set(key,data,-1L);
    }


    public void del(String streamId){
        Map<String, Object> map = get(streamId);
        if(map == null){
            return;
        }
        //设置15秒过期
        RedisUtils.set(getKey(streamId),map,15L);
    }


    private String getKey(String streamId){
        return String.format("%s_%s",VIDEO_RECORD_MP4_INFO,streamId);
    }

}
