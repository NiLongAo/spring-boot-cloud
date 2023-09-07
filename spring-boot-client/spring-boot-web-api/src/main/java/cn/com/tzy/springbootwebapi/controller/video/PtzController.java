package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.PtzService;
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
@RestController("WebApiVideoPtzController")
@RequestMapping(value = "/webapi/video/ptz")
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
}
