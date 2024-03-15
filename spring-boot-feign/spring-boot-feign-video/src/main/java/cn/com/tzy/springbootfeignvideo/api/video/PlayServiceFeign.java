package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 视频点播
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/play",configuration = FeignConfiguration.class)
public interface PlayServiceFeign {

    /**
     * 开始点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    @RequestMapping(value = "/start",method = RequestMethod.GET)
    RestResult<?> start(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId);


    /**
     * 停止点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    @RequestMapping(value = "/stop",method = RequestMethod.GET)
    RestResult<?> stop(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId);

    /**
     * 视频流转码 （非h264 转为 h264）
     * @param streamId 视频流ID
     * @return
     */
    @RequestMapping(value = "/convert",method = RequestMethod.GET)
    RestResult<?> convert(@RequestParam("streamId") String streamId);

    /**
     * 结束转码
     * @param key 视频流key
     * @param mediaServerId 流媒体服务ID
     * @return
     */
    @RequestMapping(value = "/convert_stop",method = RequestMethod.GET)
    RestResult<?> convertStop(@RequestParam("key") String key, @RequestParam("mediaServerId")String mediaServerId);
}
