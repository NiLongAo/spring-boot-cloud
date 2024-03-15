package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 国标流相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/gb/video",configuration = FeignConfiguration.class)
public interface GbVideoServiceFeign {

    /**
     * 录像查询列表
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    RestResult<?> list(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime);

    /**
     * 开始下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param downloadSpeed 下载倍速
     * @return
     */
    @RequestMapping(value = "/download/start",method = RequestMethod.GET)
    RestResult<?> start(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime, @RequestParam("downloadSpeed")Integer downloadSpeed);


    /**
     * 停止下载录像
     * @param deviceGbId 设备国标号
     * @param channelId 通道国标号
     * @param stream 流ID
     * @return
     */
    @RequestMapping(value = "/download/stop",method = RequestMethod.GET)
    RestResult<?> stop(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("stream")String stream);

    /**
     * 获取当前用户下载录像信息
     */
    @RequestMapping(value = "/download/list",method = RequestMethod.GET)
    RestResult<?> list();

    /**
     * 清除用户下载录像
     */
    @RequestMapping(value = "/download/del",method = RequestMethod.DELETE)
    RestResult<?> del(@RequestParam("key") String key);


}
