package cn.com.tzy.springbootwebapi.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.AudioPushServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 语音推流等相关接口
 */
@Service
public class AudioPushService {

    @Resource
    private AudioPushServiceFeign audioPushServiceFeign;

    /**
     * 获取语音对讲推流地址
     */
    public RestResult<?> findAudioPushPath(String deviceId,String channelId){
        return audioPushServiceFeign.findAudioPushPath(deviceId,channelId);
    }

    /**
     * 语音广播命令
     * @param deviceId 设备国标编号
     * @return
     */
    public RestResult<?> broadcast(String deviceId,String channelId){
        return audioPushServiceFeign.broadcast(deviceId,channelId);
    }

    public RestResult<?> stopAudioPush(String deviceId, String channelId) {
        return audioPushServiceFeign.stopAudioPush(deviceId,channelId);
    }
}
