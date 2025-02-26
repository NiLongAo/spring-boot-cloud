package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * 云台控制
 */
@Log4j2
@RestController("ApiPtzController")
@RequestMapping(value = "/api/ptz")
public class PtzController extends ApiController {

    @Resource
    private SIPCommander sipCommander;

    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private SipServer sipServer;

    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param cmdCode 控制指令 指令码(对应国标文档指令格式中的字节4)
     * @param parameter1 参数1 数据一(对应国标文档指令格式中的字节5, 范围0-255)
     * @param parameter2 参数2 数据二(对应国标文档指令格式中的字节6, 范围0-255)
     * @param combindCode2 缩放速度 组合码二(对应国标文档指令格式中的字节7, 范围0-15)
     * 预置位设置 129 0 预设位号 0;预置位调用 130 0 预设位号 0;预置位删除 131 0 预设位号 0
     * 巡航组添加点 132 巡航组号 预设位号 0;巡航组删除点 133 巡航组号 预设位号 0;巡航组删除组 133 巡航组号 0 0; 巡航组巡航速度 134 巡航组号 速度%256 (速度/256)*16; 巡航组停留时间 135 巡航组号 时间%256 (时间/256)*16; 巡航操作 136 巡航组号 0 0;
     * 扫描组左边界 137 扫描组号 1 0;扫描组右边界 137 扫描组号 2 0;扫描组扫描 137 扫描组号 0 0;扫描组扫描速度 138 扫描组号 速度%256 (速度/256)*16;
     * @return
     */
    @GetMapping("/front_end_command")
    public RestResult<?> frontEndCommand(@RequestParam("deviceId") String deviceId,
                                         @RequestParam("channelId") String channelId,
                                         @RequestParam(value = "cmdCode",defaultValue = "0") Integer cmdCode,
                                         @RequestParam(value = "parameter1",defaultValue = "0") Integer parameter1,
                                         @RequestParam(value = "parameter2",defaultValue = "0") Integer parameter2,
                                         @RequestParam(value = "combindCode2",defaultValue = "0") Integer combindCode2){
        log.debug(String.format("设备云台控制 API调用，deviceId：%s ，channelId：%s ，cmdCode：%d parameter1：%d parameter2：%d",deviceId, channelId, cmdCode, parameter1, parameter2));
        if (parameter1 == null || parameter1 < 0 || parameter1 > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"parameter1 为 0-255的数字");
        }
        if (parameter2 == null || parameter2 < 0 || parameter2 > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"parameter2 为 0-255的数字");
        }
        if (combindCode2 == null || combindCode2 < 0 || combindCode2 > 15) {
            return RestResult.result(RespCode.CODE_2.getValue(),"combindCode2 为 0-15的数字");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        try {
            sipCommander.frontEndCmd(sipServer,deviceVo, channelId, cmdCode, parameter1, parameter2, combindCode2,null,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 前端控制: {}", e.getMessage());
            return RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败");
        }
        return RestResult.result(RespCode.CODE_0);
    }

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
    @GetMapping("/ptz")
    public RestResult<?> ptz(@RequestParam("deviceId") String deviceId,
                          @RequestParam("channelId") String channelId,
                          @RequestParam("command") String command,
                          @RequestParam(value = "horizonSpeed",defaultValue = "0") Integer horizonSpeed,
                          @RequestParam(value = "verticalSpeed",defaultValue = "0") Integer verticalSpeed,
                          @RequestParam(value = "zoomSpeed",defaultValue = "0") Integer zoomSpeed
    ){
        log.info(String.format("设备云台控制 API调用，deviceId：%s ，channelId：%s ，command：%s ，horizonSpeed：%d ，verticalSpeed：%d ，zoomSpeed：%d",deviceId, channelId, command, horizonSpeed, verticalSpeed, zoomSpeed));
        if (horizonSpeed == null) {
            horizonSpeed = 100;
        }else if (horizonSpeed < 0 || horizonSpeed > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"horizonSpeed 为 0-255的数字");
        }
        if (verticalSpeed == null) {
            verticalSpeed = 100;
        }else if (verticalSpeed < 0 || verticalSpeed > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"verticalSpeed 为 0-255的数字");
        }
        if (zoomSpeed == null) {
            zoomSpeed = 16;
        }else if (zoomSpeed < 0 || zoomSpeed > 15) {
            return RestResult.result(RespCode.CODE_2.getValue(),"zoomSpeed 为 0-15的数字");
        }
        int cmdCode = 0;
        switch (command){
            case "left":
                cmdCode = 2;
                break;
            case "right":
                cmdCode = 1;
                break;
            case "up":
                cmdCode = 8;
                break;
            case "down":
                cmdCode = 4;
                break;
            case "upleft":
                cmdCode = 10;
                break;
            case "upright":
                cmdCode = 9;
                break;
            case "downleft":
                cmdCode = 6;
                break;
            case "downright":
                cmdCode = 5;
                break;
            case "zoomin":
                cmdCode = 16;
                break;
            case "zoomout":
                cmdCode = 32;
                break;
            case "stop":
                horizonSpeed = 0;
                verticalSpeed = 0;
                zoomSpeed = 0;
                break;
            default:
                break;
        }
        return frontEndCommand(deviceId, channelId, cmdCode, horizonSpeed, verticalSpeed, zoomSpeed);
    }

    /**
     * 光圈控制
     * @param deviceId
     * @param channelId
     * @param command
     * @param speed
     */
    @GetMapping("/iris")
    public RestResult<?> iris(@RequestParam("deviceId") String deviceId, @RequestParam("channelId") String channelId, @RequestParam("command") String command, @RequestParam("speed") Integer speed){
        if (log.isDebugEnabled()) {
            log.debug("设备光圈控制 API调用，deviceId：{} ，channelId：{} ，command：{} ，speed：{} ",deviceId, channelId, command, speed);
        }
        if (speed == null) {
            speed = 100;
        }else if (speed < 0 || speed > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"speed 为 0-255的数字");
        }

        int cmdCode = 0x40;
        switch (command){
            case "in":
                cmdCode = 0x44;
                break;
            case "out":
                cmdCode = 0x48;
                break;
            case "stop":
                speed = 0;
                break;
            default:
                break;
        }
        return frontEndCommand(deviceId, channelId, cmdCode, 0, speed, 0);
    }
    /**
     * 聚焦控制
     */
    @GetMapping("/focus")
    public RestResult<?> focus(@RequestParam("deviceId") String deviceId, @RequestParam("channelId") String channelId, @RequestParam("command") String command, @RequestParam("speed") Integer speed){
        if (log.isDebugEnabled()) {
            log.debug("设备聚焦控制 API调用，deviceId：{} ，channelId：{} ，command：{} ，speed：{} ",deviceId, channelId, command, speed);
        }
        if (speed == null) {
            speed = 100;
        }else if (speed < 0 || speed > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"speed 为 0-255的数字");
        }
        int cmdCode = 0x40;
        switch (command){
            case "near":
                cmdCode = 0x42;
                break;
            case "far":
                cmdCode = 0x41;
                break;
            case "stop":
                speed = 0;
                break;
            default:
                break;
        }
        return frontEndCommand(deviceId, channelId, cmdCode, speed, 0, 0);
    }

    /**
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    @GetMapping("/preset_query")
    public  DeferredResult<RestResult> presetQuery(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId){
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s",DeferredResultHolder.CALLBACK_CMD_PRESETQUERY,ObjectUtils.isEmpty(channelId) ? deviceId : channelId);
        VideoRestResult<RestResult> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        if (deferredResultHolder.exist(key, null)) {
            return result;
        }
        deferredResultHolder.put(key,uuid,result);
        try {
            sipCommander.presetQuery(sipServer,deviceVo, channelId,null, error -> {
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(error.getStatusCode(),error.getMsg()));
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            log.error("[命令发送失败] 获取设备预置位: {}", e.getMessage());
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败"));
        }
        return result;
    }
    /**
     * 预置位指令-设置预置位
     */
    @GetMapping("/add_preset")
    public RestResult<?> addPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId) {
        if (presetId == null || presetId < 1 || presetId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"预置位编号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x81, 1, presetId, 0);
    }

    /**
     * 预置位指令-调用预置位
     */
    @GetMapping("/call_preset")
    public RestResult<?> callPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId) {
        if (presetId == null || presetId < 1 || presetId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"预置位编号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x82, 1, presetId, 0);
    }

    /**
     * 预置位指令-删除预置位
     */
    @GetMapping("/del_preset")
    public RestResult<?> delPreset(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId) {
        if (presetId == null || presetId < 1 || presetId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"预置位编号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x83, 1, presetId, 0);
    }

    /**
     * 巡航指令-加入巡航点
     */
    @GetMapping("/add_cruise_point")
    public RestResult<?> addCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId, @RequestParam("cruiseId") Integer cruiseId) {
        if (presetId == null || presetId < 1 || presetId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"预置位编号必须为1-255之间的数字, 为0时删除整个巡航");
        }
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x84, cruiseId, presetId, 0);
    }

    /**
     * 巡航指令-删除一个巡航点
     */
    @GetMapping("/del_cruise_point")
    public RestResult<?> delCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("presetId") Integer presetId, @RequestParam("cruiseId") Integer cruiseId) {
        if (presetId == null || presetId < 1 || presetId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"预置位编号必须为1-255之间的数字, 为0时删除整个巡航");
        }
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x85, cruiseId, presetId, 0);
    }

    /**
     * 巡航指令-设置巡航速度
     */
    @GetMapping("/speed_cruise_point")
    public RestResult<?> speedCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId, @RequestParam("speed") Integer speed) {
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        if (speed == null || speed < 1 || speed > 4095) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航速度必须为1-4095之间的数字");
        }
        int parameter2 = speed & 0xFF;
        int combindCode2 =  speed >> 8;
        return frontEndCommand(deviceId, channelId, 0x86, cruiseId, parameter2, combindCode2);
    }

    /**
     * 巡航指令-设置巡航停留时间
     */
    @GetMapping("/time_cruise_point")
    public RestResult<?> timeCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId, @RequestParam("time") Integer time) {
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        if (time == null || time < 1 || time > 4095) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航停留时间必须为1-4095之间的数字");
        }
        int parameter2 = time & 0xFF;
        int combindCode2 =  time >> 8;
        return frontEndCommand(deviceId, channelId, 0x87, cruiseId, parameter2, combindCode2);
    }

    /**
     * 巡航指令-开始巡航
     */
    @GetMapping("/start_cruise_point")
    public RestResult<?> startCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId) {
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x88, cruiseId, 0, 0);
    }

    /**
     * 巡航指令-停止巡航
     */
    @GetMapping("/stop_cruise_point")
    public RestResult<?> stopCruisePoint(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("cruiseId") Integer cruiseId) {
        if (cruiseId == null || cruiseId < 1 || cruiseId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"巡航组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0, 0, 0, 0);
    }

    /**
     * 扫描指令-开始自动扫描
     */
    @GetMapping("/start_scan")
    public RestResult<?> startScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId) {
        if (scanId == null || scanId < 1 || scanId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"扫描组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x89, scanId, 0, 0);
    }

    /**
     * 扫描指令-停止自动扫描
     */
    @GetMapping("/stop_scan")
    public RestResult<?> stopScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId) {
        if (scanId == null || scanId < 1 || scanId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"扫描组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0, 0, 0, 0);
    }

    /**
     * 扫描指令-设置自动扫描左边界
     */
    @GetMapping("/set_left_scan")
    public RestResult<?> setLeftScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId) {
        if (scanId == null || scanId < 1 || scanId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"扫描组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x89, scanId, 1, 0);
    }

    /**
     * 扫描指令-设置自动扫描右边界
     */
    @GetMapping("/set_right_scan")
    public RestResult<?> setRightScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId) {
        if (scanId == null || scanId < 1 || scanId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"扫描组号必须为1-255之间的数字");
        }
        return frontEndCommand(deviceId, channelId, 0x89, scanId, 2, 0);
    }

    /**
     * 扫描指令-设置自动扫描速度
     */
    @GetMapping("/set_speed_scan")
    public RestResult<?> setSpeedScan(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("scanId") Integer scanId, @RequestParam("speed") Integer speed) {
        if (scanId == null || scanId < 1 || scanId > 255) {
            return RestResult.result(RespCode.CODE_2.getValue(),"扫描组号必须为1-255之间的数字");
        }
        if (speed == null || speed < 1 || speed > 4095) {
            return RestResult.result(RespCode.CODE_2.getValue(),"自动扫描速度必须为1-4095之间的数字");
        }
        int parameter2 = speed & 0xFF;
        int combindCode2 =  speed >> 8;
        return frontEndCommand(deviceId, channelId, 0x8A, scanId, parameter2, combindCode2);
    }

    /**
     * 辅助开关控制指令-雨刷控制
     */
    @GetMapping("/wiper")
    public RestResult<?> wiper(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("command") String command) {
        if (log.isDebugEnabled()) {
            log.debug("辅助开关控制指令-雨刷控制 API调用，deviceId：{} ，channelId：{} ，command：{}",deviceId, channelId, command);
        }
        int cmdCode = 0;
        switch (command){
            case "on":
                cmdCode = 0x8c;
                break;
            case "off":
                cmdCode = 0x8d;
                break;
            default:
                break;
        }
        return frontEndCommand(deviceId, channelId, cmdCode, 1, 0, 0);
    }

    /**
     * 辅助开关控制指令-雨刷控制
     */
    @GetMapping("/auxiliary")
    public RestResult<?> auxiliary(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId, @RequestParam("command") String command,@RequestParam("switchId") Integer switchId) {
        if (log.isDebugEnabled()) {
            log.debug("辅助开关控制指令-雨刷控制 API调用，deviceId：{} ，channelId：{} ，command：{}, switchId: {}",deviceId, channelId, command, switchId);
        }
        int cmdCode = 0;
        switch (command){
            case "on":
                cmdCode = 0x8c;
                break;
            case "off":
                cmdCode = 0x8d;
                break;
            default:
                break;
        }
        return frontEndCommand(deviceId, channelId, cmdCode, switchId, 0, 0);
    }

}
