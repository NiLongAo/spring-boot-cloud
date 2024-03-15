package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.model.PageModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 设备通道相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/device",configuration = FeignConfiguration.class)
public interface DeviceServiceFeign {

    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody PageModel param);


    /**
     * 根据国标设备编号获取设备
     */
    @RequestMapping(value = "/find_device_id",method = RequestMethod.GET)
    RestResult<?> findDeviceId(@RequestParam("deviceId") String deviceId);

    /**
     * 移除设备
     */
    @RequestMapping(value = "/del",method = RequestMethod.DELETE)
    RestResult<?> del(@RequestParam("deviceId") String deviceId);

    /**
     * 修改数据流传输模式
     * @param deviceId 设备编号
     * @param streamMode 传输协议 1.UDP 2.TCP
     * @return
     */
    @RequestMapping(value = "/update_transport",method = RequestMethod.GET)
    RestResult<?> updateTransport(@RequestParam("deviceId") String deviceId,@RequestParam("streamMode") Integer streamMode);

    /**
     * 设备保存
     * @param param 设备信息
     * @return
     */
    @RequestMapping(value = "/save_device",method = RequestMethod.POST)
    RestResult<?> saveDevice(@Validated @RequestBody Device param);

    /**
     * 设备状态查询
     */
    @RequestMapping(value = "/find_device_status",method = RequestMethod.GET)
    RestResult<?> findDeviceStatus(@RequestParam("deviceId") String deviceId);

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
    @RequestMapping(value = "/find_device_alarm",method = RequestMethod.GET)
    RestResult<?> findDeviceAlarm(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(name = "startPriority",required = false) String startPriority,
            @RequestParam(name = "endPriority",required = false) String endPriority,
            @RequestParam(name = "alarmMethod",required = false) String alarmMethod,
            @RequestParam(name = "alarmType",required = false) String alarmType,
            @RequestParam(name = "startTime",required = false) String startTime,
            @RequestParam(name = "endTime",required = false) String endTime
    );

    /**
     * 获取设备的订阅状态
     * @param deviceId 设备id
     * @return
     */
    @RequestMapping(value = "/subscribe_info",method = RequestMethod.GET)
    RestResult<?> subscribeInfo(@RequestParam("deviceId") String deviceId);


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
    @RequestMapping(value = "/basic_param",method = RequestMethod.GET)
    RestResult<?> basicParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "name",required = false)String name,
            @RequestParam(name = "expiration",required = false)String expiration,
            @RequestParam(name = "heartBeatInterval",required = false)String heartBeatInterval,
            @RequestParam(name = "heartBeatCount",required = false)String heartBeatCount
    );

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
    @RequestMapping(value = "/query_param",method = RequestMethod.GET)
    RestResult<?> queryParam(
            @RequestParam("deviceId") String deviceId,
            @RequestParam(name = "channelId",required = false)String channelId,
            @RequestParam(name = "configType",required = false)String configType
    );

    /**
     * 远程启动控制
     */
    @RequestMapping(value = "/start_control",method = RequestMethod.GET)
    RestResult<?> startControl(@RequestParam("deviceId") String deviceId);

    /**
     * 录像控制命令
     * @param deviceId 设备id
     * @param channelId 通道编号
     * @param status 操作状态 0.停止 1.启用
     * @return
     */
    @RequestMapping(value = "/record_control",method = RequestMethod.GET)
    RestResult<?> recordControl(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId, @RequestParam("status")Integer status);

    /**
     * 布防/撤防命令
     * @param deviceId 设备id
     * @param status 操作状态 0.撤防 1.布防
     * @return
     */
    @RequestMapping(value = "/guard_control",method = RequestMethod.GET)
    RestResult<?> guardControl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId,@RequestParam("status")Integer status);

    /**
     * 报警复位
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param alarmMethod 报警方式
     * @param alarmType 报警类型
     * @return
     */
    @RequestMapping(value = "/reset_alarm",method = RequestMethod.GET)
    RestResult<?> resetAlarm(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId, @RequestParam(name = "alarmMethod",required = false)String alarmMethod, @RequestParam(name = "alarmType",required = false)String alarmType);

    /**
     * 强制关键帧
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @return
     */
    @RequestMapping(value = "/i_frame",method = RequestMethod.GET)
    RestResult<?> iFrame(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId);

    /**
     * 看守位控制
     * @param deviceId 设备id
     * @param channelId 通道国标编号
     * @param enabled 看守位使能1:开启,0:关闭
     * @param resetTime 自动归位时间间隔，开启看守位时使用，单位:秒(s)
     * @param presetIndex 调用预置位编号，开启看守位时使用，取值范围0~255
     * @return
     */
    @RequestMapping(value = "/home_position",method = RequestMethod.GET)
    RestResult<?> homePosition(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "enabled",required = false)Integer enabled,
            @RequestParam(name = "resetTime",required = false)Integer resetTime,
            @RequestParam(name = "presetIndex",required = false)Integer presetIndex
    );

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
    @RequestMapping(value = "/zoom_in",method = RequestMethod.GET)
    RestResult<?> zoomIn(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "length",defaultValue = "0")Integer length,
            @RequestParam(name = "width",defaultValue = "0")Integer width,
            @RequestParam(name = "midpointx",defaultValue = "0")Integer midpointx,
            @RequestParam(name = "midpointy",defaultValue = "0")Integer midpointy,
            @RequestParam(name = "lengthx",defaultValue = "0")Integer lengthx,
            @RequestParam(name = "lengthy",defaultValue = "0")Integer lengthy
    );


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
    @RequestMapping(value = "/zoom_out",method = RequestMethod.GET)
    RestResult<?> zoomOut(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId")String channelId,
            @RequestParam(name = "length",defaultValue = "0")Integer length,
            @RequestParam(name = "width",defaultValue = "0")Integer width,
            @RequestParam(name = "midpointx",defaultValue = "0")Integer midpointx,
            @RequestParam(name = "midpointy",defaultValue = "0")Integer midpointy,
            @RequestParam(name = "lengthx",defaultValue = "0")Integer lengthx,
            @RequestParam(name = "lengthy",defaultValue = "0")Integer lengthy
    );
}
