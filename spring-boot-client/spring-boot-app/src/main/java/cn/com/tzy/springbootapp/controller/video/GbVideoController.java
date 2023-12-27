package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.GbVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "国标录像相关信息接口",position = 4)
@RestController("AppVideoGbVideoController")
@RequestMapping(value = "/app/video/gb/video/download")
public class GbVideoController  extends ApiController {
    @Resource
    private GbVideoService gbVideoService;

    @ApiOperation(value = "录像查询列表", notes = "录像查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="startTime", value="开始时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="endTime", value="结束时间", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/list")
    public RestResult<?> list(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime){
        return gbVideoService.list(deviceGbId,channelId,startTime,endTime);
    }

    @ApiOperation(value = "开始下载录像", notes = "开始下载录像")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="startTime", value="开始时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="endTime", value="结束时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="downloadSpeed", value="下载倍速", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @GetMapping("/start")
    public RestResult<?>start(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("startTime")String startTime, @RequestParam("endTime")String endTime,@RequestParam("downloadSpeed")Integer downloadSpeed){
        return gbVideoService.start(deviceGbId,channelId,startTime,endTime,downloadSpeed);
    }

    @ApiOperation(value = "停止下载录像", notes = "停止下载录像")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="stream", value="流编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/stop")
    public RestResult<?> stop(@RequestParam("deviceGbId") String deviceGbId, @RequestParam("channelId")String channelId, @RequestParam("stream")String stream){
        return gbVideoService.stop(deviceGbId,channelId,stream);
    }

    @ApiOperation(value = "获取当前用户下载录像信息", notes = "获取当前用户下载录像信息")
    @GetMapping("/user_list")
    public RestResult<?> userList(){
        return gbVideoService.list();
    }

    @ApiOperation(value = "清除用户下载录像", notes = "清除用户下载录像")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key", value="流编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/del")
    public RestResult<?> del(@RequestParam("key") String key){
        return gbVideoService.del(key);
    }
}
