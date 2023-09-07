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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
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
                break;
            default:
                break;
        }
        try {
            sipCommander.frontEndCmd(sipServer,deviceVo, channelId, cmdCode, horizonSpeed, verticalSpeed, zoomSpeed,null,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 云台控制: {}", e.getMessage());
            return RestResult.result(RespCode.CODE_2.getValue(),"命令发送失败");
        }
        return RestResult.result(RespCode.CODE_0);
    }
    /**
     * 云台控制
     * @param deviceId 设备国标编号
     * @param channelId 通道编号
     * @param cmdCode 控制指令
     * @param parameter1 参数1
     * @param parameter2 参数2
     * @param combindCode2 缩放速度
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
}
