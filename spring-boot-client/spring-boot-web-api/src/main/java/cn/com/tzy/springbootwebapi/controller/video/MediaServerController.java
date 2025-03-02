package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.MediaServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "流媒体相关信息接口",position = 4)
@RestController("WebApiMediaServerController")
@RequestMapping(value = "/webapi/video/media/server")
public class MediaServerController extends ApiController {

    @Resource
    private MediaServerService mediaServerService;


    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody MediaServerPageParam param){
        return mediaServerService.page(param);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key", value="流媒体编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") String id) {
        return mediaServerService.detail(id);
    }

    @ApiOperation(value = "保存", notes = "保存")
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody MediaServerSaveParam param){
        return mediaServerService.save(param);
    }

    @ApiOperation(value = "移除", notes = "移除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key", value="流媒体编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") String id) {
        return mediaServerService.remove(id);
    }

    @ApiOperation(value = "根据应用名和流id获取播放地址", notes = "根据应用名和流id获取播放地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deviceId", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="channelId", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("find_play_url")
    public RestResult<?> findPlayUrl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return mediaServerService.findPlayUrl(deviceId,channelId);
    }

    @ApiOperation(value = "获取流信息", notes = "获取流信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="app", value="设备编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="stream", value="通道编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="mediaServerId", value="流媒体信息", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("find_media_info")
    public RestResult<?> findMediaInfo(@RequestParam("app") String app,@RequestParam("stream")String stream,@RequestParam("mediaServerId")String mediaServerId){
        return mediaServerService.findMediaInfo(app,stream,mediaServerId);
    }
}
