package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 视频流关联到级联平台
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/playback",configuration = FeignConfiguration.class)
public interface PlaybackServiceFeign {

    /**
     * 播放视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/start",method = RequestMethod.GET)
    RestResult<?> start(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    );

    /**
     * 停止视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param streamId 流ID
     * @return
     */
    @RequestMapping(value = "/stop",method = RequestMethod.GET)
    RestResult<?> stop(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("streamId") String streamId
    );

    /**
     * 暂停视频回放
     * @param streamId 流ID
     * @return
     */
    @RequestMapping(value = "/suspend",method = RequestMethod.GET)
    RestResult<?> suspend(@RequestParam("streamId") String streamId);

    /**
     * 暂停回放恢复
     * @param streamId 流ID
     * @return
     */
    @RequestMapping(value = "/restore",method = RequestMethod.GET)
    RestResult<?> restore(@RequestParam("streamId") String streamId);

    /**
     * 回放拖动播放
     * @param streamId 流ID
     * @param seekTime 拖动偏移量，单位s
     * @return
     */
    @RequestMapping(value = "/seek",method = RequestMethod.GET)
    RestResult<?> seek(@RequestParam("streamId") String streamId,@RequestParam(value = "seekTime",defaultValue = "0")Long seekTime);

    /**
     * 回放倍速播放
     * @param streamId 流ID
     * @param speed 倍速0.25 0.5 1、2、4
     * @return
     */
    @RequestMapping(value = "/speed",method = RequestMethod.GET)
    RestResult<?> speed(@RequestParam("streamId") String streamId,@RequestParam(value = "speed",defaultValue = "0")Double speed);

}
