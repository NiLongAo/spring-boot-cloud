package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.StreamPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "推流相关信息接口",position = 4)
@RestController("WebApiVideoStreamPushController")
@RequestMapping(value = "/webapi/video/stream/push")
public class StreamPushController extends ApiController {

    @Resource
    private StreamPushService streamPushService;

    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody StreamPushPageParam param){
        return streamPushService.page(param);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="上级平台编号", required=true, paramType="query", dataType="long", example="0"),
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return streamPushService.detail(id);
    }

    @ApiOperation(value = "保存", notes = "保存")
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody StreamPushSaveParam param){
        return streamPushService.save(param);
    }

    @ApiOperation(value = "移除", notes = "移除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="推流代理编号", required=true, paramType="query", dataType="Long", example="0"),
    })
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id){
        return streamPushService.remove(id);
    }

    @ApiOperation(value = "获取推流播放地址", notes = "获取推流播放地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="推流代理编号", required=true, paramType="query", dataType="Long", example="0"),
    })
    @GetMapping( "get_play_url")
    public RestResult<?> getPlayUrl(@RequestParam("id") Long id){
        return streamPushService.getPlayUrl(id);
    }

}
