package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.AudioPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "语音推流等相关接口",position = 4)
@RestController("WebApiVideoAudioPushController")
@RequestMapping(value = "/webapi/video/audio/push")
public class AudioPushController extends ApiController {

    @Resource
    private AudioPushService audioPushService;

    @ApiOperation(value = "获取语音对讲推流地址", notes = "获取语音对讲推流地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("audio_push_path")
    public RestResult<?> findAudioPushPath(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId) {
        return  audioPushService.findAudioPushPath(deviceId,channelId);
    }

    @ApiOperation(value = "语音广播命令", notes = "语音广播命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("/broadcast")
    public RestResult<?> broadcast(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return audioPushService.broadcast(deviceId,channelId);
    }

    @ApiOperation(value = "停止推流", notes = "停止推流")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("/stop_audio_push")
    public RestResult<?> stopAudioPush(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return audioPushService.stopAudioPush(deviceId,channelId);
    }

}
