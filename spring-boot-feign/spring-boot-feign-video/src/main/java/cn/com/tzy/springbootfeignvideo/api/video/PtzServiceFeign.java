package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 云台控制
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/ptz",configuration = FeignConfiguration.class)
public interface PtzServiceFeign {

    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param command 控制指令
     * @param horizonSpeed 水平移动速度
     * @param verticalSpeed 垂直移动速度
     * @param zoomSpeed 缩放速度
     * @return
     */
    @RequestMapping(value = "/ptz",method = RequestMethod.GET)
    RestResult<?> ptz(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("command") String command,
            @RequestParam(value = "horizonSpeed",defaultValue = "0") Integer horizonSpeed,
            @RequestParam(value = "verticalSpeed",defaultValue = "0") Integer verticalSpeed,
            @RequestParam(value = "zoomSpeed",defaultValue = "0") Integer zoomSpeed
    );

    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param cmdCode 控制指令
     * @param parameter1 水平移动速度
     * @param parameter2 垂直移动速度
     * @param combindCode2 缩放速度
     * @return
     */
    @RequestMapping(value = "/front_end_command",method = RequestMethod.GET)
    RestResult<?> frontEndCommand(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam(value = "cmdCode",defaultValue = "0") Integer cmdCode,
            @RequestParam(value = "parameter1",defaultValue = "0") Integer parameter1,
            @RequestParam(value = "parameter2",defaultValue = "0") Integer parameter2,
            @RequestParam(value = "combindCode2",defaultValue = "0") Integer combindCode2
    );
    /**
     * 光圈控制
     * @return
     */
    @RequestMapping(value ="/iris",method = RequestMethod.GET)
    RestResult<?> iris(@RequestParam("deviceId") String deviceId, @RequestParam("channelId") String channelId, @RequestParam("command") String command, @RequestParam("speed") Integer speed);

    /**
     * 聚焦控制
     * @return
     */
    @RequestMapping(value ="/focus",method = RequestMethod.GET)
    RestResult<?> focus(@RequestParam("deviceId") String deviceId, @RequestParam("channelId") String channelId, @RequestParam("command") String command, @RequestParam("speed") Integer speed);

    /**
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/preset_query",method = RequestMethod.GET)
    RestResult<?> presetQuery(@RequestParam("deviceId")String deviceId, @RequestParam("channelId")String channelId);
    /**
     * 预置位指令-设置预置位
     * @return
     */
    @RequestMapping(value = "/add_preset",method = RequestMethod.GET)
    RestResult<?> addPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId);

    /**
     * 预置位指令-调用预置位
     * @return
     */
    @RequestMapping(value = "/call_preset",method = RequestMethod.GET)
    RestResult<?> callPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId);

    /**
     * 预置位指令-删除预置位
     * @return
     */
    @RequestMapping(value = "/del_preset",method = RequestMethod.GET)
    RestResult<?> delPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId);

    /**
     * 巡航指令-加入巡航点
     * @return
     */
    @RequestMapping(value = "/add_cruise_point",method = RequestMethod.GET)
    RestResult<?> addCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId, @RequestParam("cruiseId") Integer cruiseId);

    /**
     * 巡航指令-删除一个巡航点
     * @return
     */
    @RequestMapping(value = "/del_cruise_point",method = RequestMethod.GET)
    RestResult<?> delCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId, @RequestParam("cruiseId") Integer cruiseId);

    /**
     * 巡航指令-设置巡航速度
     * @return
     */
    @RequestMapping(value = "/speed_cruise_point",method = RequestMethod.GET)
    RestResult<?> speedCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId, @RequestParam("speed") Integer speed);

    /**
     * 巡航指令-设置巡航停留时间
     * @return
     */
    @RequestMapping(value = "/time_cruise_point",method = RequestMethod.GET)
    RestResult<?> timeCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId, @RequestParam("time") Integer time);

    /**
     * 巡航指令-开始巡航
     * @return
     */
    @RequestMapping(value = "/start_cruise_point",method = RequestMethod.GET)
    RestResult<?> startCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId);

    /**
     * 巡航指令-停止巡航
     * @return
     */
    @RequestMapping(value = "/stop_cruise_point",method = RequestMethod.GET)
    RestResult<?> stopCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId);

    /**
     * 扫描指令-开始自动扫描
     * @return
     */
    @RequestMapping(value = "/start_scan",method = RequestMethod.GET)
    RestResult<?> startScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId);

    /**
     * 扫描指令-停止自动扫描
     * @return
     */
    @RequestMapping(value = "/stop_scan",method = RequestMethod.GET)
    RestResult<?> stopScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId);

    /**
     * 扫描指令-设置自动扫描左边界
     * @return
     */
    @RequestMapping(value = "/set_left_scan",method = RequestMethod.GET)
    RestResult<?> setLeftScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId);

    /**
     * 扫描指令-设置自动扫描右边界
     * @return
     */
    @RequestMapping(value = "/set_right_scan",method = RequestMethod.GET)
    RestResult<?> setRightScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId);

    /**
     * 扫描指令-设置自动扫描速度
     * @return
     */
    @RequestMapping(value = "/set_speed_scan",method = RequestMethod.GET)
    RestResult<?> setSpeedScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId, @RequestParam("speed") Integer speed);

    /**
     * 辅助开关控制指令-雨刷控制
     * @return
     */
    @RequestMapping(value = "/wiper",method = RequestMethod.GET)
    RestResult<?> wiper(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("command") String command);

    /**
     * 辅助开关控制指令-辅助控制
     * @return
     */
    @RequestMapping(value = "/auxiliary",method = RequestMethod.GET)
    RestResult<?> auxiliary(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("command") String command,@RequestParam("switchId") Integer switchId);

}
