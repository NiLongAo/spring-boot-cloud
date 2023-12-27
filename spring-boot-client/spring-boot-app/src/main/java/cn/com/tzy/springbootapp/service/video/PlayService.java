package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.PlayServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频点播
 */
@Service
public class PlayService {
    
    @Resource
    private PlayServiceFeign playServiceFeign;

    /**
     * 开始点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    public RestResult<?> start(String deviceId, String channelId){
        return playServiceFeign.start(deviceId,channelId);
    }


    /**
     * 停止点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    public RestResult<?> stop(String deviceId, String channelId){
        return playServiceFeign.stop(deviceId,channelId);
    }

    /**
     * 视频流转码 （非h264 转为 h264）
     * @param streamId 视频流ID
     * @return
     */
    public RestResult<?> convert(String streamId){
        return playServiceFeign.convert(streamId);
    }

    /**
     * 结束转码
     * @param key 视频流key
     * @param mediaServerId 流媒体服务ID
     * @return
     */
    public RestResult<?> convertStop(String key,String mediaServerId){
        return playServiceFeign.convertStop(key,mediaServerId);
    }



}
