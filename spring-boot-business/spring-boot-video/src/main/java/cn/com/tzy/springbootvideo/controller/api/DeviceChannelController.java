package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.DeviceChannelService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 设备通道相关接口
 */
@Log4j2
@RestController("ApiDeviceChannelController")
@RequestMapping(value = "/api/device/channel")
public class DeviceChannelController extends ApiController {

    @Resource
    private DeviceChannelService deviceChannelService;

    /**
     * 设备通道树
     */
    @GetMapping("/tree")
    public RestResult<?> tree(){
        boolean administrator = JwtUtils.getAdministrator();
        return  deviceChannelService.findTreeDeviceChannel(administrator);
    }

    /**
     * 通道分页 44010200501010000001
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody DeviceChannelPageParam param) {
        return  deviceChannelService.findPage(param);
    }

    /**
     * 通道详情
     */
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("channelId") String channelId){
        return  deviceChannelService.detail(channelId);
    }

    /**
     * 保存通道信息
     */
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody DeviceChannel param){
        return  deviceChannelService.saveDeviceChannel(param);
    }

    /**
     * 同步设备通道
     */
    @GetMapping("sync")
    public RestResult<?> sync(@RequestParam("deviceId") String deviceId){
        return  deviceChannelService.sync(deviceId);
    }

    /**
     * 删除通道
     */
    @DeleteMapping("del")
    public RestResult<?> del(@RequestParam("deviceId") String deviceId,@RequestParam("channelId") String channelId)  {
        return  deviceChannelService.del(deviceId,channelId);
    }

    /**
     * 获取通道同步进度
     */
    @GetMapping("sync_status")
    public RestResult<?> syncStatus(@RequestParam("deviceId") String deviceId) {
        return  deviceChannelService.syncStatus(deviceId);
    }


}
