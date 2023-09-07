package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.PlayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "视频点播",position = 4)
@RestController("WebApiVideoPlayController")
@RequestMapping(value = "/webapi/video/play")
public class PlayController extends ApiController {

    @Resource
    private PlayService playService;

    @ApiOperation(value = "开始点播", notes = "开始点播")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/start")
    public RestResult<?> start(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId){
        return playService.start(deviceId,channelId);
    }

    @ApiOperation(value = "停止点播", notes = "停止点播")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道国标编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/stop")
    public RestResult<?> stop(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId){
        return playService.stop(deviceId,channelId);
    }

    @ApiOperation(value = "视频流转码 （非h264 转为 h264）", notes = "视频流转码 （非h264 转为 h264）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="streamId", value="视频流ID", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/convert")
    public RestResult<?> convert(@RequestParam("streamId") String streamId){
        return playService.convert(streamId);
    }

    @ApiOperation(value = "结束转码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key", value="视频流key", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="mediaServerId", value="流媒体服务ID", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/convert_stop")
    public RestResult<?> convertStop(@RequestParam("key") String key, @RequestParam("mediaServerId")String mediaServerId){
        return playService.convertStop(key,mediaServerId);
    }

}
