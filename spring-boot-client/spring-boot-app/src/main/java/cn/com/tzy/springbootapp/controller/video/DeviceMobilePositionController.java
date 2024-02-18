package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.DeviceMobilePositionService;
import com.alibaba.csp.sentinel.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "设备位置信息管理",position = 4)
@RestController("AppVideoDeviceMobilePositionController")
@RequestMapping(value = "/app/video/device/mobile_position")
public class DeviceMobilePositionController extends ApiController {

    @Resource
    private DeviceMobilePositionService deviceMobilePositionService;

    @ApiOperation(value = "查询历史轨迹", notes = "查询历史轨迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="start", value="开始时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="end", value="结束时间", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/history")
    public RestResult<?> history(@RequestParam("deviceId") String deviceId,@RequestParam(name = "channelId",required = false) String channelId, @RequestParam(name = "start",required = false) String start,@RequestParam(name = "end",required = false) String end){
        if (StringUtil.isEmpty(start)) {
            start = null;
        }
        if (StringUtil.isEmpty(end)) {
            end = null;
        }
        return deviceMobilePositionService.history(deviceId, channelId, start, end);
    }

    @ApiOperation(value = "查询设备最新位置", notes = "查询设备最新位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/latest")
    public RestResult<?> latest(@RequestParam("deviceId") String deviceId){
        return deviceMobilePositionService.latest(deviceId);
    }

    @ApiOperation(value = "获取移动位置信息", notes = "获取移动位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/realtime")
    public RestResult<?> realtime(@RequestParam("deviceId") String deviceId,@RequestParam(name = "channelId",required = false) String channelId){
        return deviceMobilePositionService.realtime(deviceId,channelId);
    }

    @ApiOperation(value = "订阅位置信息", notes = "订阅位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="type", value="订阅类型", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="expires", value="订阅时间", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/subscribe")
    public RestResult<?> subscribe(@RequestParam("deviceId") String deviceId,@RequestParam("type") Integer type,@RequestParam("expires") Integer expires){
        return deviceMobilePositionService.subscribe(deviceId,type,expires);
    }

    @ApiOperation(value = "数据位置信息格式处理", notes = "数据位置信息格式处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })

    @GetMapping("/transform")
    public RestResult<?> transform(@RequestParam("deviceId") String deviceId){
        return deviceMobilePositionService.transform(deviceId);
    }

}
