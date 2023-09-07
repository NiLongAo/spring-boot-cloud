package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.PlatformGbChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "上级平台国标通道相关接口",position = 4)
@RestController("WebApiVideoPlatformGbChannelController")
@RequestMapping(value = "/webapi/video/platform/gb_channel")
public class PlatformGbChannelController extends ApiController {

    @Resource
    private PlatformGbChannelService platformGbChannelService;

    @ApiOperation(value = "国标级联通道列表", notes = "国标级联通道列表")
    @GetMapping("device_channel_list")
    public RestResult<?> findDeviceChannelList(){
        return platformGbChannelService.findDeviceChannelList();
    }

    @ApiOperation(value = "国标级联绑定的通道key集合", notes = "国标级联绑定的通道key集合")
    @PostMapping("channel_bind_key")
    public RestResult<?> findChannelBindKey(@Validated @RequestBody PlatformGbChannelParam param){
        return platformGbChannelService.findChannelBindKey(param);
    }

    @ApiOperation(value = "向上级平台添加国标通道", notes = "向上级平台添加国标通道")
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param){
        return platformGbChannelService.insert(param);
    }

    @ApiOperation(value = "从上级平台移除国标通道", notes = "从上级平台移除国标通道")
    @PostMapping("delete")
    public RestResult<?> delete(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param){
        return platformGbChannelService.delete(param);
    }

}
