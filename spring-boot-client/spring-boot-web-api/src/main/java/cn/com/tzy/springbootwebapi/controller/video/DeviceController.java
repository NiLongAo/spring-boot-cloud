package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "设备相关接口",position = 4)
@RestController("WebApiVideoDeviceController")
@RequestMapping(value = "/webapi/video/device")
public class DeviceController extends ApiController {

    @Resource
    private DeviceService deviceService;


    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("/page")
    public PageResult page(@Validated @RequestBody PageModel param){
        return  deviceService.page(param);
    }


    @ApiOperation(value = "根据国标设备编号获取设备", notes = "根据国标设备编号获取设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("/find_device_id")
    public RestResult<?> findDeviceId(@RequestParam("deviceId") String deviceId){
        return  deviceService.findDeviceId(deviceId);
    }

    @ApiOperation(value = "移除设备", notes = "移除设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @DeleteMapping("/del")
    public RestResult<?> del(@RequestParam("deviceId") String deviceId){
        return  deviceService.del(deviceId);
    }

    @ApiOperation(value = "修改数据流传输模式", notes = "修改数据流传输模式")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="streamMode", value="传输协议 1.UDP 2.TCP", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping("/update_transport")
    public RestResult<?> updateTransport(@RequestParam("deviceId") String deviceId,@RequestParam("streamMode") Integer streamMode){
        return deviceService.updateTransport(deviceId,streamMode);
    }

    @ApiOperation(value = "设备保存", notes = "设备保存")
    @PostMapping("/save_device")
    public RestResult<?> saveDevice(@Validated @RequestBody Device param){
        return deviceService.saveDevice(param);
    }

    @ApiOperation(value = "设备状态查询", notes = "设备状态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("find_device_status")
    public RestResult<?> findDeviceStatus(@RequestParam("deviceId") String deviceId){
        return deviceService.findDeviceStatus(deviceId);
    }

    @ApiOperation(value = "设备报警查询", notes = "设备报警查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="startPriority", value="报警起始级别（可选）", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="endPriority", value="报警终止级别（可选）", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="alarmMethod", value="报警方式条件（可选）", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="alarmType", value="报警类型", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="startTime", value="报警发生起始时间（可选）", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="endTime", value="报警发生终止时间（可选）", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("find_device_alarm")
    public RestResult<?> findDeviceAlarm(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(name = "startPriority",required = false) String startPriority,
            @RequestParam(name = "endPriority",required = false) String endPriority,
            @RequestParam(name = "alarmMethod",required = false) String alarmMethod,
            @RequestParam(name = "alarmType",required = false) String alarmType,
            @RequestParam(name = "startTime",required = false) String startTime,
            @RequestParam(name = "endTime",required = false) String endTime
    ){
        return deviceService.findDeviceAlarm(deviceId,startPriority,endPriority,alarmMethod,alarmType,startTime,endTime);
    }


    @ApiOperation(value = "获取设备的订阅状态", notes = "获取设备的订阅状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("subscribe_info")
    public RestResult<?> subscribeInfo(@RequestParam("deviceId") String deviceId){
        return deviceService.subscribeInfo(deviceId);
    }
    /**************************************************以下为设备配置操作相关API******************************************************************/

    @ApiOperation(value = "基本配置设置命令", notes = "基本配置设置命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="name", value="名称", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="expiration", value="到期时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="heartBeatInterval", value="心跳间隔", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="heartBeatCount", value="心跳计数", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("basic_param")
    public RestResult<?> basicParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "expiration",required = false)String expiration,
            @RequestParam(name = "heartBeatInterval",required = false)String heartBeatInterval,
            @RequestParam(name = "heartBeatCount",required = false)String heartBeatCount
    ){
        return  deviceService.basicParam(deviceId,channelId,name,expiration,heartBeatInterval,heartBeatCount);
    }

    @ApiOperation(value = "基本配置设置命令", notes = "基本配置设置命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="configType", value="配置类型 基本参数配置:BasicParam,视频参数范围:VideoParamOpt,SVAC编码配置:SVACEncodeConfig, SVAC解码配置:SVACDecodeConfig", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("query_param")
    public RestResult<?> queryParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(name = "channelId",required = false)String channelId,
            @RequestParam(name = "configType",required = false)String configType
    ){
        return  deviceService.queryParam(deviceId,channelId,configType);
    }


    /**************************************************以下为设备控制操作相关API******************************************************************/

    @ApiOperation(value = "远程启动控制", notes = "远程启动控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("start_control")
    public RestResult<?> startControl(@RequestParam("deviceId") String deviceId){
        return  deviceService.startControl(deviceId);
    }

    @ApiOperation(value = "录像控制命令", notes = "录像控制命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="status", value="操作状态 0.停止 1.启用", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("record_control")
    public RestResult<?> recordControl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam("status")Integer status){
        return  deviceService.recordControl(deviceId,channelId,status);
    }

    @ApiOperation(value = "布防/撤防命令", notes = "布防/撤防命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="status", value="操作状态 0.撤防 1.布防", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("guard_control")
    public RestResult<?> guardControl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam("status")Integer status){
        return  deviceService.guardControl(deviceId,channelId,status);
    }

    @ApiOperation(value = "报警复位", notes = "报警复位")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="alarmMethod", value="报警方式", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="alarmType", value="报警类型", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("reset_alarm")
    public RestResult<?> resetAlarm(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam(name = "alarmMethod",required = false)String alarmMethod,@RequestParam(name = "alarmType",required = false)String alarmType){
        return  deviceService.resetAlarm(deviceId,channelId,alarmMethod,alarmType);
    }

    @ApiOperation(value = "强制关键帧", notes = "强制关键帧")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("i_frame")
    public RestResult<?> iFrame(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return  deviceService.iFrame(deviceId,channelId);
    }

    @ApiOperation(value = "看守位控制", notes = "看守位控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="enabled", value="看守位使能1:开启,0:关闭", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="resetTime", value="自动归位时间间隔，开启看守位时使用，单位:秒(s)", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="presetIndex", value="调用预置位编号，开启看守位时使用，取值范围0~255", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("home_position")
    public RestResult<?> homePosition(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "enabled",required = false)Integer enabled,
            @RequestParam(name = "resetTime",required = false)Integer resetTime,
            @RequestParam(name = "presetIndex",required = false)Integer presetIndex
    ){
        return  deviceService.homePosition(deviceId,channelId,enabled,resetTime,presetIndex);
    }

    @ApiOperation(value = "拉框放大", notes = "拉框放大")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="length", value="播放窗口长度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="width", value="播放窗口宽度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="midpointx", value="拉框中心的横轴坐标像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="midpointy", value="拉框中心的纵轴坐标像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="lengthx", value="拉框长度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="lengthy", value="拉框宽度像素值", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("zoom_in")
    public RestResult<?> zoomIn(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "length",defaultValue = "0")Integer length,
            @RequestParam(name = "width",defaultValue = "0")Integer width,
            @RequestParam(name = "midpointx",defaultValue = "0")Integer midpointx,
            @RequestParam(name = "midpointy",defaultValue = "0")Integer midpointy,
            @RequestParam(name = "lengthx",defaultValue = "0")Integer lengthx,
            @RequestParam(name = "lengthy",defaultValue = "0")Integer lengthy
    ){
        return  deviceService.zoomIn(deviceId,channelId,length,width,midpointx,midpointy,lengthx,lengthy);
    }

    @ApiOperation(value = "拉框缩小", notes = "拉框缩小")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="length", value="播放窗口长度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="width", value="播放窗口宽度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="midpointx", value="拉框中心的横轴坐标像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="midpointy", value="拉框中心的纵轴坐标像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="lengthx", value="拉框长度像素值", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="lengthy", value="拉框宽度像素值", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("zoom_out")
    public RestResult<?> zoomOut(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "length",defaultValue = "0")Integer length,
            @RequestParam(name = "width",defaultValue = "0")Integer width,
            @RequestParam(name = "midpointx",defaultValue = "0")Integer midpointx,
            @RequestParam(name = "midpointy",defaultValue = "0")Integer midpointy,
            @RequestParam(name = "lengthx",defaultValue = "0")Integer lengthx,
            @RequestParam(name = "lengthy",defaultValue = "0")Integer lengthy
    ){
        return  deviceService.zoomOut(deviceId,channelId,length,width,midpointx,midpointy,lengthx,lengthy);
    }
}
