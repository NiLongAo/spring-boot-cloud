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
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    @RequestMapping(value = "/preset_query",method = RequestMethod.GET)
    RestResult<?> presetQuery(@RequestParam("deviceId")String deviceId, @RequestParam("channelId")String channelId);



}
