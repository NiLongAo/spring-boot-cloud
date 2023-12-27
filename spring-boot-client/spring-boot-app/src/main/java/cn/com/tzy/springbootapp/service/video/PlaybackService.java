package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootfeignvideo.api.video.PlaybackServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频流关联到级联平台
 */
@Service
public class PlaybackService {

    @Resource
    private PlaybackServiceFeign playbackServiceFeign;

    /**
     * 播放视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public RestResult<?> start(String deviceId,String channelId,String startTime,String endTime){
         return playbackServiceFeign.start(deviceId,channelId,startTime,endTime);
    }

    /**
     * 停止视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param streamId 流ID
     * @return
     */
    public RestResult<?> stop(String deviceId, String channelId,String streamId){
        return playbackServiceFeign.stop(deviceId,channelId,streamId);
    }

    /**
     * 暂停视频回放
     * @param streamId 流ID
     * @return
     */
    public RestResult<?> suspend(String streamId){
        return playbackServiceFeign.suspend(streamId);
    }

    /**
     * 暂停回放恢复
     * @param streamId 流ID
     * @return
     */
    public RestResult<?> restore(String streamId){
        return playbackServiceFeign.restore(streamId);
    }

    /**
     * 回放拖动播放
     * @param streamId 流ID
     * @param seekTime 拖动偏移量，单位s
     * @return
     */
    public RestResult<?> seek(String streamId,Long seekTime){
        return playbackServiceFeign.seek(streamId,seekTime);
    }

    /**
     * 回放倍速播放
     * @param streamId 流ID
     * @param speed 倍速0.25 0.5 1、2、4
     * @return
     */
    public RestResult<?> speed(String streamId,Double speed){
        return playbackServiceFeign.speed(streamId,speed);
    }

}
