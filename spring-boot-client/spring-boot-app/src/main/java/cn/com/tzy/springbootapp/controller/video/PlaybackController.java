package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.PlaybackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "视频回放",position = 4)
@RestController("AppVideoPlaybackController")
@RequestMapping(value = "/app/video/playback")
public class PlaybackController extends ApiController {

    @Resource
    private PlaybackService playbackService;


    @ApiOperation(value = "播放视频回放", notes = "播放视频回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="startTime", value="开始时间", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="endTime", value="结束时间", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/start")
    public RestResult<?> start(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    ){
        return playbackService.start(deviceId,channelId,startTime,endTime);
    }

    @ApiOperation(value = "停止视频回放", notes = "停止视频回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="streamId", value="流ID", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/stop")
    public RestResult<?> stop(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("streamId") String streamId
    ){
        return playbackService.stop(deviceId,channelId,streamId);
    }

    @ApiOperation(value = "暂停视频回放", notes = "暂停视频回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name="streamId", value="流ID", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/suspend")
    public RestResult<?> suspend(@RequestParam("streamId") String streamId){
        return playbackService.suspend(streamId);
    }


    @ApiOperation(value = "暂停回放恢复", notes = "暂停回放恢复")
    @ApiImplicitParams({
            @ApiImplicitParam(name="streamId", value="流ID", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/restore")
    public RestResult<?> restore(@RequestParam("streamId") String streamId){
        return playbackService.restore(streamId);
    }

    @ApiOperation(value = "回放拖动播放", notes = "回放拖动播放")
    @ApiImplicitParams({
            @ApiImplicitParam(name="streamId", value="流ID", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="seekTime", value="拖动偏移量，单位s", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping("/seek")
    public RestResult<?> seek(@RequestParam("streamId") String streamId,@RequestParam(value = "seekTime",defaultValue = "0")Long seekTime){
        return playbackService.seek(streamId,seekTime);
    }

    @ApiOperation(value = "回放倍速播放", notes = "回放倍速播放")
    @ApiImplicitParams({
            @ApiImplicitParam(name="streamId", value="流ID", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="speed", value="倍速0.25 0.5 1、2、4", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping("/speed")
    public RestResult<?> speed(@RequestParam("streamId") String streamId,@RequestParam(value = "speed",defaultValue = "0")Double speed){
        return playbackService.speed(streamId,speed);
    }
}
