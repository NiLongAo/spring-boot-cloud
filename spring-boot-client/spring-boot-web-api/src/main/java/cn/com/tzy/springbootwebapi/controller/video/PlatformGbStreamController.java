package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.PlatformGbStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "视频流关联到级联平台",position = 4)
@RestController("WebApiVideoPlatformGbStreamController")
@RequestMapping(value = "/webapi/video/platform/gb_stream")
public class PlatformGbStreamController extends ApiController {

    @Resource
    private PlatformGbStreamService platformGbStreamService;


    @ApiOperation(value = "级联视频流列表", notes = "级联视频流列表")
    @GetMapping("gb_stream_list")
    public RestResult<?> findGbStreamList(){
        return platformGbStreamService.findGbStreamList();
    }

    @ApiOperation(value = "级联视频流关联列表", notes = "级联视频流关联列表")
    @PostMapping("stream_bind_key")
    public RestResult<?> findStreamBindKey(@Validated @RequestBody PlatformGbStreamParam param){
        return platformGbStreamService.findStreamBindKey(param);
    }


    @ApiOperation(value = "添加关联平台国标流信息", notes = "添加关联平台国标流信息")
    @PostMapping("add")
    public RestResult<?> add(@Validated @RequestBody PlatformGbStreamSaveParam param){
        return platformGbStreamService.add(param);
    }

    @ApiOperation(value = "移除关联平台国标流信息", notes = "移除关联平台国标流信息")
    @PostMapping("del")
    public RestResult<?> del(@Validated @RequestBody PlatformGbStreamSaveParam param){
        return platformGbStreamService.del(param);
    }

}
