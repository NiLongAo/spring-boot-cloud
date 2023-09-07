package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 设备通道相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/device/mobile_position",configuration = FeignConfiguration.class)
public interface DeviceMobilePositionServiceFeign {


    /**
     * 查询历史轨迹
     * @param deviceId 设备ID
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    @RequestMapping(value = "/history",method = RequestMethod.GET)
    RestResult<?> history(@RequestParam("deviceId") String deviceId,@RequestParam(name = "channelId",required = false) String channelId, @RequestParam(name = "start",required = false) String start,@RequestParam(name = "end",required = false) String end);


    /**
     * 查询设备最新位置
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/latest",method = RequestMethod.GET)
    RestResult<?> latest(@RequestParam("deviceId") String deviceId);

    /**
     * 获取移动位置信息
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/realtime",method = RequestMethod.GET)
    RestResult<?> realtime(@RequestParam("deviceId") String deviceId, @RequestParam(name = "channelId",required = false) String channelId);

    /**
     * 订阅位置信息
     * @param deviceId 设备ID
     * @param expires 目录订阅周期
     * @param interval 位置订阅周期
     */
    @RequestMapping(value = "/subscribe",method = RequestMethod.GET)
    RestResult<?> subscribe(@RequestParam("deviceId") String deviceId,@RequestParam("expires") Integer expires,@RequestParam("interval") Integer interval);


    /**
     * 数据位置信息格式处理
     * @param deviceId 设备ID
     * @return
     */
    @RequestMapping(value = "/transform",method = RequestMethod.GET)
    RestResult<?> transform(@RequestParam("deviceId") String deviceId);

}
