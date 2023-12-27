package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.StreamProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "拉流相关信息接口",position = 4)
@RestController("AppVideoStreamProxyController")
@RequestMapping(value = "/app/video/stream/proxy")
public class StreamProxyController  extends ApiController {

    @Resource
    private StreamProxyService streamProxyService;

    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody StreamProxyPageParam param){
        return streamProxyService.page(param);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="上级平台编号", required=true, paramType="query", dataType="long", example="0"),
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return streamProxyService.detail(id);
    }

    @ApiOperation(value = "保存", notes = "保存")
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody StreamProxySaveParam param){
        return streamProxyService.save(param);
    }

    @ApiOperation(value = "移除", notes = "移除")
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id) {
        return streamProxyService.remove(id);
    }

    @ApiOperation(value = "获取流媒体中ffmpeg.cmd模板", notes = "获取流媒体中ffmpeg.cmd模板")
    @GetMapping("find_ffmpeg_cmd")
    public RestResult<?> findFfmpegCmd(@RequestParam("mediaServerId")String mediaServerId){
        return streamProxyService.findFfmpegCmd(mediaServerId);
    }

    @ApiOperation(value = "启用代理", notes = "启用代理")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="拉流代理编号", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping("start")
    public RestResult<?> start(@RequestParam("id") Long id){
        return streamProxyService.start(id);
    }
    /**
     * 停用代理
     */
    @ApiOperation(value = "停用代理", notes = "停用代理")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="拉流代理编号", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping("stop")
    public RestResult<?> stop(@RequestParam("id") Long id){
        return streamProxyService.stop(id);
    }

    @ApiOperation(value = "获取拉流播放地址", notes = "获取拉流播放地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="拉流代理编号", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping( "get_play_url")
    public RestResult<?> getPlayUrl(@RequestParam("id") Long id){
        return streamProxyService.getPlayUrl(id);
    }
}
