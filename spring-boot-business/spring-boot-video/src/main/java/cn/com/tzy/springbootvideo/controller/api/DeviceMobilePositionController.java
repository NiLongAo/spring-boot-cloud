package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.DeviceMobilePositionService;
import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

/**
 * 设备位置信息管理
 */
@Log4j2
@RestController("ApiDeviceMobilePositionController")
@RequestMapping(value = "/api/device/mobile_position")
public class DeviceMobilePositionController extends ApiController {

    @Resource
    private DeviceMobilePositionService deviceMobilePositionService;

    /**
     * 查询历史轨迹
     * @param deviceId 设备ID
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    @GetMapping("/history")
    public RestResult<?> history(@RequestParam("deviceId") String deviceId,@RequestParam(name = "channelId",required = false) String channelId, @RequestParam(name = "start",required = false) String start,@RequestParam(name = "end",required = false) String end){
        if (StringUtil.isEmpty(start)) {
            start = null;
        }
        if (StringUtil.isEmpty(end)) {
            end = null;
        }
        return deviceMobilePositionService.findHistoryMobilePositions(deviceId, channelId, start, end);
    }

    /**
     * 查询设备最新位置
     * @param deviceId 设备ID
     * @return
     */
    @GetMapping("/latest")
    public RestResult<?> latest(@RequestParam("deviceId") String deviceId){
        return deviceMobilePositionService.findLatestMobilePositions(deviceId);
    }

    /**
     * 获取移动位置信息
     * @param deviceId 设备ID
     * @return
     */
    @GetMapping("/realtime")
    public DeferredResult<RestResult<?>> realtime(@RequestParam("deviceId") String deviceId,@RequestParam(name = "channelId",required = false) String channelId){
        return deviceMobilePositionService.findRealtime(deviceId,channelId);
    }

    /**
     * 订阅位置信息
     * @param deviceId 设备ID
     * @param expires 目录订阅周期
     * @param interval 位置订阅周期
     */
    @GetMapping("/subscribe")
    public RestResult<?> subscribe(@RequestParam("deviceId") String deviceId,@RequestParam("expires") Integer expires,@RequestParam("interval") Integer interval){
        return deviceMobilePositionService.subscribe(deviceId,expires,interval);
    }

    /**
     * 数据位置信息格式处理
     * @param deviceId 设备ID
     * @return
     */
    @GetMapping("/transform")
    public RestResult<?> transform(@RequestParam("deviceId") String deviceId){
        return deviceMobilePositionService.transform(deviceId);
    }

}
