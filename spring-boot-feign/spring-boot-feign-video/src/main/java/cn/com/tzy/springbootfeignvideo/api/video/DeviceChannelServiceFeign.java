package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备通道相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/device/channel",configuration = FeignConfiguration.class)
public interface DeviceChannelServiceFeign {

    /**
     * 通道分页
     */
    @RequestMapping(value = "/tree",method = RequestMethod.GET)
    RestResult<?> tree();
    /**
     * 通道分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody DeviceChannelPageParam param);

    /**
     * 获取通道同步进度
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("channelId") String channelId);

    /**
     * 保存通道信息
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    RestResult<?> save(@Validated @RequestBody DeviceChannel param);

    /**
     * 同步设备通道
     */
    @RequestMapping(value = "/sync",method = RequestMethod.GET)
    RestResult<?> sync(@RequestParam("deviceId") String deviceId);

    /**
     * 获取通道同步进度
     */
    @RequestMapping(value = "/sync_status",method = RequestMethod.GET)
    RestResult<?> syncStatus(@RequestParam("deviceId") String deviceId);

    /**
     * 删除通道
     */
    @RequestMapping(value = "/del",method = RequestMethod.DELETE)
    RestResult<?> del(@RequestParam("deviceId") String deviceId,@RequestParam("channelId") String channelId);

}
