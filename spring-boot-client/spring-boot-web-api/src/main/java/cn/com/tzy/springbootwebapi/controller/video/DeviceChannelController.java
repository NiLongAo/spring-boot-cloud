package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.param.video.DeviceChannelPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.DeviceChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "设备通道相关接口",position = 4)
@RestController("WebApiVideoDeviceChannelController")
@RequestMapping(value = "/webapi/video/device/channel")
public class DeviceChannelController extends ApiController {

    @Resource
    private DeviceChannelService deviceChannelService;

    @ApiOperation(value = "获取设备通道树", notes = "获取设备通道树")
    @GetMapping("tree")
    public RestResult<?> tree() {
        return  deviceChannelService.tree();
    }

    @ApiOperation(value = "通道分页", notes = "通道分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody DeviceChannelPageParam param){
        return  deviceChannelService.page(param);
    }


    @ApiOperation(value = "保存通道信息", notes = "保存通道信息")
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody DeviceChannel param){
        return  deviceChannelService.save(param);
    }

    /**
     * 通道详情
     */
    @ApiOperation(value = "通道详情", notes = "通道详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("channelId") String channelId){
        return  deviceChannelService.detail(channelId);
    }

    /**
     * 同步设备通道
     */
    @ApiOperation(value = "同步设备通道", notes = "同步设备通道")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("sync")
    public RestResult<?> sync(@RequestParam("deviceId") String deviceId){
        return  deviceChannelService.sync(deviceId);
    }


    @ApiOperation(value = "获取通道同步进度", notes = "获取通道同步进度")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("sync_status")
    public RestResult<?> syncStatus(@RequestParam("deviceId") String deviceId) {
        return  deviceChannelService.syncStatus(deviceId);
    }
    @ApiOperation(value = "删除通道", notes = "删除通道")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @DeleteMapping("del")
    public RestResult<?> del(@RequestParam("deviceId") String deviceId,@RequestParam("channelId") String channelId) {
        return  deviceChannelService.del(deviceId,channelId);
    }
}
