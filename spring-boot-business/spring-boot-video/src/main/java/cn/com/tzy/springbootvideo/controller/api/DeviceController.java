package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.DeviceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

/**
 * 设备相关接口
 */
@Log4j2
@RestController("ApiDeviceController")
@RequestMapping(value = "/api/device")
public class DeviceController extends ApiController {

    @Resource
    private DeviceService deviceService;

    /**
     * 设备分页
     */
    @PostMapping("/page")
    public PageResult page(@Validated @RequestBody PageModel param){
        return  deviceService.findPage(param);
    }

    /**
     * 根据国标设备编号获取设备
     */
    @GetMapping("/find_device_id")
    public RestResult<?> findDeviceId(@RequestParam("deviceId") String deviceId){
        return  deviceService.findDeviceId(deviceId);
    }

    /**
     * 移除设备
     */
    @DeleteMapping("/del")
    public RestResult<?> del(@RequestParam("deviceId") String deviceId) throws Exception {
        return  deviceService.del(deviceId);
    }

    /**
     * 修改数据流传输模式
     * @param deviceId 设备编号
     * @param streamMode 传输协议 1.UDP 2.TCP
     * @return
     */
    @GetMapping("/update_transport")
    public RestResult<?> updateTransport(@RequestParam("deviceId") String deviceId,@RequestParam("streamMode") Integer streamMode){
        return deviceService.updateTransport(deviceId,streamMode);
    }

    /**
     * 设备保存
     * @param param 设备信息
     * @return
     */
    @PostMapping("/save_device")
    public RestResult<?> saveDevice(@Validated @RequestBody Device param){
        return deviceService.saveDevice(param);
    }

    /**
     * 设备状态查询
     */
    @GetMapping("find_device_status")
    public DeferredResult<RestResult<?>> findDeviceStatus(@RequestParam("deviceId") String deviceId){
        return deviceService.findDeviceStatus(deviceId);
    }

    /**
     * 设备报警查询
     * @param deviceId 设备id
     * @param startPriority 报警起始级别（可选）
     * @param endPriority 报警终止级别（可选）
     * @param alarmMethod 报警方式条件（可选）
     * @param alarmType 报警类型
     * @param startTime 报警发生起始时间（可选）
     * @param endTime 报警发生终止时间（可选）
     * @return
     */
    @GetMapping("find_device_alarm")
    public DeferredResult<RestResult<?>> findDeviceAlarm(
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

    /**
     * 获取设备的订阅状态
     * @param deviceId 设备id
     * @return
     */
    @GetMapping("subscribe_info")
    public RestResult<?> subscribeInfo(@RequestParam("deviceId") String deviceId){
        return deviceService.subscribeInfo(deviceId);
    }
    /**************************************************以下为设备配置操作相关API******************************************************************/

    /**
     * 基本配置设置命令
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param name 名称
     * @param expiration 到期时间
     * @param heartBeatInterval 心跳间隔
     * @param heartBeatCount 心跳计数
     * @return
     */
    @GetMapping("basic_param")
    public DeferredResult<RestResult<?>> basicParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "expiration",required = false)String expiration,
            @RequestParam(name = "heartBeatInterval",required = false)String heartBeatInterval,
            @RequestParam(name = "heartBeatCount",required = false)String heartBeatCount
    ){
        return  deviceService.basicParam(deviceId,channelId,name,expiration,heartBeatInterval,heartBeatCount);
    }
    /**
     * 设备配置查询请求
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param configType 配置类型 查询配置参数类型(必选),可查询的配置类型包括
     *                   基本参数配置:BasicParam,
     *                   视频参数范围:VideoParamOpt,
     *                   SVAC编码配置:SVACEncodeConfig
     *                   SVAC解码配置:SVACDecodeConfig
     * 应,每个响应对应一个配置类型。
     * @return
     */
    @GetMapping("query_param")
    public DeferredResult<RestResult<?>> queryParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(name = "channelId",required = false)String channelId,
            @RequestParam(name = "configType",required = false)String configType
    ){
        return  deviceService.queryParam(deviceId,channelId,configType);
    }


    /**************************************************以下为设备控制操作相关API******************************************************************/


    /**
     * 远程启动控制
     */
    @GetMapping("start_control")
    public RestResult<?> startControl(@RequestParam("deviceId") String deviceId){
        return  deviceService.startControl(deviceId);
    }

    /**
     * 录像控制命令
     * @param deviceId 设备id
     * @param channelId 通道编号
     * @param status 操作状态 0.停止 1.启用
     * @return
     */
    @GetMapping("record_control")
    public DeferredResult<RestResult<?>> recordControl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam("status")Integer status){
        return  deviceService.recordControl(deviceId,channelId,status);
    }

    /**
     * 布防/撤防命令
     * @param deviceId 设备id
     * @param status 操作状态 0.撤防 1.布防
     * @return
     */
    @GetMapping("guard_control")
    public DeferredResult<RestResult<?>> guardControl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam("status")Integer status){
        return  deviceService.guardControl(deviceId,channelId,status);
    }
    /**
     * 报警复位
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param alarmMethod 报警方式
     * @param alarmType 报警类型
     * @return
     */
    @GetMapping("reset_alarm")
    public DeferredResult<RestResult<?>> resetAlarm(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam(name = "alarmMethod",required = false)String alarmMethod,@RequestParam(name = "alarmType",required = false)String alarmType){
        return  deviceService.resetAlarm(deviceId,channelId,alarmMethod,alarmType);
    }
    /**
     * 强制关键帧
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @return
     */
    @GetMapping("i_frame")
    public DeferredResult<RestResult<?>> iFrame(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return  deviceService.iFrame(deviceId,channelId);
    }

    /**
     * 看守位控制
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param enabled 看守位使能1:开启,0:关闭
     * @param resetTime 自动归位时间间隔，开启看守位时使用，单位:秒(s)
     * @param presetIndex 调用预置位编号，开启看守位时使用，取值范围0~255
     * @return
     */
    @GetMapping("home_position")
    public DeferredResult<RestResult<?>> homePosition(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "enabled",required = false)Integer enabled,
            @RequestParam(name = "resetTime",required = false)Integer resetTime,
            @RequestParam(name = "presetIndex",required = false)Integer presetIndex
    ){
        return  deviceService.homePosition(deviceId,channelId,enabled,resetTime,presetIndex);
    }
    /**
     * 拉框放大
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param length 播放窗口长度像素值
     * @param width 播放窗口宽度像素值
     * @param midpointx 拉框中心的横轴坐标像素值
     * @param midpointy 拉框中心的纵轴坐标像素值
     * @param lengthx 拉框长度像素值
     * @param lengthy 拉框宽度像素值
     * @return
     */
    @GetMapping("zoom_in")
    public DeferredResult<RestResult<?>> zoomIn(
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


    /**
     * 拉框缩小
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param length 播放窗口长度像素值
     * @param width 播放窗口宽度像素值
     * @param midpointx 拉框中心的横轴坐标像素值
     * @param midpointy 拉框中心的纵轴坐标像素值
     * @param lengthx 拉框长度像素值
     * @param lengthy 拉框宽度像素值
     * @return
     */
    @GetMapping("zoom_out")
    public DeferredResult<RestResult<?>> zoomOut(
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
