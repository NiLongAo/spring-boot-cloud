package cn.com.tzy.springbootstarterfreeswitch.redis.impl;

import cn.com.tzy.springbootstarterfreeswitch.common.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.PlaybackInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaybackInfoManager {

    private String FS_PLAYBACK_INFO = RedisConstant.FS_PLAYBACK_INFO;


    public void put(PlaybackInfo groupInfo){
        if(groupInfo == null ){
            return;
        }
        RedisUtils.set(getKey(groupInfo.getId()),groupInfo,-1L);
    }


    public PlaybackInfo get(String id) {
        List<String> scan = RedisUtils.keys(getKey(id));
        if (!scan.isEmpty()) {
            return (PlaybackInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }


    public void del(String id){
        RedisUtils.del(getKey(id));
    }

    private String getKey(String id){
        return String.format("%s%s",FS_PLAYBACK_INFO,id);
    }
}
