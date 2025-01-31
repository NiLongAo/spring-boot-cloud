package cn.com.tzy.springbootstarterfreeswitch.redis.impl.media;

import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.InviteInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class RecordMp4Manager {


    private String VIDEO_RECORD_MP4_INFO = SipConstant.VIDEO_RECORD_MP4_INFO;


    public Map<String,Object> get(String streamId){
       return (Map<String,Object>) RedisUtils.get(getKey(streamId));
    }

    public void put(String streamId, InviteInfo inviteInfo){
        if(inviteInfo == null || inviteInfo.getStreamInfo() == null){
            return;
        }
        String key = getKey(streamId);
        Map<Object, Object> data = new HashMap<>();
        data.put("type",inviteInfo.getTypeName());
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
