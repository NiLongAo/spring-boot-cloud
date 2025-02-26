package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.PtzService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "云台控制",position = 4)
@RestController("AppVideoPtzController")
@RequestMapping(value = "/app/video/ptz")
public class PtzController extends ApiController {

    @Resource
    private PtzService ptzService;

    @ApiOperation(value = "云台控制", notes = "云台控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="command", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="horizonSpeed", value="水平移动速度", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="verticalSpeed", value="垂直移动速度", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="zoomSpeed", value="缩放速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/ptz")
    public RestResult<?> ptz(@RequestParam("deviceId") String deviceId,
                          @RequestParam("channelId") String channelId,
                          @RequestParam("command") String command,
                          @RequestParam(value = "horizonSpeed",defaultValue = "0") Integer horizonSpeed,
                          @RequestParam(value = "verticalSpeed",defaultValue = "0") Integer verticalSpeed,
                          @RequestParam(value = "zoomSpeed",defaultValue = "0") Integer zoomSpeed
    ){
        return ptzService.ptz(deviceId,channelId,command,horizonSpeed,verticalSpeed,zoomSpeed);
    }

    @ApiOperation(value = "云台控制", notes = "云台控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="cmdCode", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="parameter1", value="水平移动速度", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="parameter2", value="垂直移动速度", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="combindCode2", value="缩放速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/front_end_command")
    public RestResult<?> frontEndCommand(@RequestParam("deviceId") String deviceId,
                                      @RequestParam("channelId") String channelId,
                                      @RequestParam(value = "cmdCode",defaultValue = "0") Integer cmdCode,
                                      @RequestParam(value = "parameter1",defaultValue = "0") Integer parameter1,
                                      @RequestParam(value = "parameter2",defaultValue = "0") Integer parameter2,
                                      @RequestParam(value = "combindCode2",defaultValue = "0") Integer combindCode2){
        return ptzService.frontEndCommand(deviceId,channelId,cmdCode,parameter1,parameter2,combindCode2);
    }

    /**
     * 预置位查询
     * @param deviceId
     * @param channelId
     * @return
     */
    @ApiOperation(value = "预置位查询", notes = "预置位查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/preset_query")
    public  RestResult<?> presetQuery(@RequestParam("deviceId")String deviceId,@RequestParam("channelId")String channelId){
        return ptzService.presetQuery(deviceId,channelId);
    }

    @ApiOperation(value = "光圈控制", notes = "光圈控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="command", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="speed", value="速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/iris")
    public RestResult<?> iris(@RequestParam("deviceId") String deviceId,
                              @RequestParam("channelId") String channelId,
                              @RequestParam("command") String command,
                              @RequestParam(value = "speed", defaultValue = "0") Integer speed) {
        return ptzService.iris(deviceId, channelId, command, speed);
    }

    @ApiOperation(value = "聚焦控制", notes = "聚焦控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="command", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="speed", value="速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/focus")
    public RestResult<?> focus(@RequestParam("deviceId") String deviceId,
                               @RequestParam("channelId") String channelId,
                               @RequestParam("command") String command,
                               @RequestParam(value = "speed", defaultValue = "0") Integer speed) {
        return ptzService.focus(deviceId, channelId, command, speed);
    }

    @ApiOperation(value = "预置位指令-设置预置位", notes = "预置位指令-设置预置位")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="presetId", value="预置位编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/add_preset")
    public RestResult<?> addPreset(@RequestParam("deviceId") String deviceId,
                                   @RequestParam("channelId") String channelId,
                                   @RequestParam("presetId") Integer presetId) {
        return ptzService.addPreset(deviceId, channelId, presetId);
    }

    @ApiOperation(value = "预置位指令-调用预置位", notes = "预置位指令-调用预置位")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="presetId", value="预置位编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/call_preset")
    public RestResult<?> callPreset(@RequestParam("deviceId") String deviceId,
                                    @RequestParam("channelId") String channelId,
                                    @RequestParam("presetId") Integer presetId) {
        return ptzService.callPreset(deviceId, channelId, presetId);
    }

    @ApiOperation(value = "预置位指令-删除预置位", notes = "预置位指令-删除预置位")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="presetId", value="预置位编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/del_preset")
    public RestResult<?> delPreset(@RequestParam("deviceId") String deviceId,
                                   @RequestParam("channelId") String channelId,
                                   @RequestParam("presetId") Integer presetId) {
        return ptzService.delPreset(deviceId, channelId, presetId);
    }

    @ApiOperation(value = "巡航指令-加入巡航点", notes = "巡航指令-加入巡航点")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="presetId", value="预置位编号", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="cruiseId", value="巡航编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/add_cruise_point")
    public RestResult<?> addCruisePoint(@RequestParam("deviceId") String deviceId,
                                        @RequestParam("channelId") String channelId,
                                        @RequestParam("presetId") Integer presetId,
                                        @RequestParam("cruiseId") Integer cruiseId) {
        return ptzService.addCruisePoint(deviceId, channelId, presetId, cruiseId);
    }

    @ApiOperation(value = "巡航指令-删除一个巡航点", notes = "巡航指令-删除一个巡航点")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="presetId", value="预置位编号", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="cruiseId", value="巡航编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/del_cruise_point")
    public RestResult<?> delCruisePoint(@RequestParam("deviceId") String deviceId,
                                        @RequestParam("channelId") String channelId,
                                        @RequestParam("presetId") Integer presetId,
                                        @RequestParam("cruiseId") Integer cruiseId) {
        return ptzService.delCruisePoint(deviceId, channelId, presetId, cruiseId);
    }

    @ApiOperation(value = "巡航指令-设置巡航速度", notes = "巡航指令-设置巡航速度")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="cruiseId", value="巡航编号", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="speed", value="速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/speed_cruise_point")
    public RestResult<?> speedCruisePoint(@RequestParam("deviceId") String deviceId,
                                          @RequestParam("channelId") String channelId,
                                          @RequestParam("cruiseId") Integer cruiseId,
                                          @RequestParam("speed") Integer speed) {
        return ptzService.speedCruisePoint(deviceId, channelId, cruiseId, speed);
    }

    @ApiOperation(value = "巡航指令-设置巡航停留时间", notes = "巡航指令-设置巡航停留时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="cruiseId", value="巡航编号", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="time", value="停留时间", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/time_cruise_point")
    public RestResult<?> timeCruisePoint(@RequestParam("deviceId") String deviceId,
                                         @RequestParam("channelId") String channelId,
                                         @RequestParam("cruiseId") Integer cruiseId,
                                         @RequestParam("time") Integer time) {
        return ptzService.timeCruisePoint(deviceId, channelId, cruiseId, time);
    }

    @ApiOperation(value = "巡航指令-开始巡航", notes = "巡航指令-开始巡航")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="cruiseId", value="巡航编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/start_cruise_point")
    public RestResult<?> startCruisePoint(@RequestParam("deviceId") String deviceId,
                                          @RequestParam("channelId") String channelId,
                                          @RequestParam("cruiseId") Integer cruiseId) {
        return ptzService.startCruisePoint(deviceId, channelId, cruiseId);
    }

    @ApiOperation(value = "巡航指令-停止巡航", notes = "巡航指令-停止巡航")
    @ApiImplicitParams({
            @ApiImplicitParam(name="设备国标编号", value="deviceId", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="通道国标编号", value="channelId", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="巡航编号", value="cruiseId", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/stop_cruise_point")
    public RestResult<?> stopCruisePoint(@RequestParam("deviceId") String deviceId,
                                         @RequestParam("channelId") String channelId,
                                         @RequestParam("cruiseId") Integer cruiseId) {
        return ptzService.stopCruisePoint(deviceId, channelId, cruiseId);
    }

    @ApiOperation(value = "扫描指令-开始自动扫描", notes = "扫描指令-开始自动扫描")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="scanId", value="扫描编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/start_scan")
    public RestResult<?> startScan(@RequestParam("deviceId") String deviceId,
                                   @RequestParam("channelId") String channelId,
                                   @RequestParam("scanId") Integer scanId) {
        return ptzService.startScan(deviceId, channelId, scanId);
    }
    @ApiOperation(value = "扫描指令-停止自动扫描", notes = "扫描指令-停止自动扫描")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="scanId", value="扫描编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/stop_scan")
    public RestResult<?> stopScan(@RequestParam("deviceId") String deviceId,
                                  @RequestParam("channelId") String channelId,
                                  @RequestParam("scanId") Integer scanId) {
        return ptzService.stopScan(deviceId, channelId, scanId);
    }

    @ApiOperation(value = "扫描指令-设置自动扫描左边界", notes = "扫描指令-设置自动扫描左边界")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="scanId", value="扫描编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/set_left_scan")
    public RestResult<?> setLeftScan(@RequestParam("deviceId") String deviceId,
                                     @RequestParam("channelId") String channelId,
                                     @RequestParam("scanId") Integer scanId) {
        return ptzService.setLeftScan(deviceId, channelId, scanId);
    }

    @ApiOperation(value = "扫描指令-设置自动扫描右边界", notes = "扫描指令-设置自动扫描右边界")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="scanId", value="扫描编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/set_right_scan")
    public RestResult<?> setRightScan(@RequestParam("deviceId") String deviceId,
                                      @RequestParam("channelId") String channelId,
                                      @RequestParam("scanId") Integer scanId) {
        return ptzService.setRightScan(deviceId, channelId, scanId);
    }

    @ApiOperation(value = "扫描指令-设置自动扫描速度", notes="扫描指令-设置自动扫描速度")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="scanId", value="扫描编号", required=true, paramType="query", dataType="Integer", example="0"),
            @ApiImplicitParam(name="speed", value="速度", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/set_speed_scan")
    public RestResult<?> setSpeedScan(@RequestParam("deviceId") String deviceId,
                                      @RequestParam("channelId") String channelId,
                                      @RequestParam("scanId") Integer scanId,
                                      @RequestParam("speed") Integer speed) {
        return ptzService.setSpeedScan(deviceId, channelId, scanId, speed);
    }

    @ApiOperation(value = "辅助开关控制指令-雨刷控制", notes = "辅助开关控制指令-雨刷控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="command", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/wiper")
    public RestResult<?> wiper(@RequestParam("deviceId") String deviceId,
                               @RequestParam("channelId") String channelId,
                               @RequestParam("command") String command) {
        return ptzService.wiper(deviceId, channelId, command);
    }

    @ApiOperation(value = "辅助开关控制指令-辅助开关控制", notes = "辅助开关控制指令-辅助开关控制")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="极tring", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="command", value="控制指令", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="switchId", value="开关编号", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/auxiliary")
    public RestResult<?> auxiliary(@RequestParam("deviceId") String deviceId,
                                   @RequestParam("channelId") String channelId,
                                   @RequestParam("command") String command,
                                   @RequestParam("switchId") Integer switchId) {
        return ptzService.auxiliary(deviceId, channelId, command, switchId);
    }
}
