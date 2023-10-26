package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 上级平台国标通道相关接口
 */
@Log4j2
@RestController("ApiPlatformGbChannelController")
@RequestMapping(value = "/api/platform/gb_channel")
public class PlatformGbChannelController extends ApiController {

    @Resource
    private PlatformGbChannelService platformGbChannelService;

    /**
     * 国标级联通道列表
     */
    @GetMapping("device_channel_list")
    public RestResult<?> findDeviceChannelList() throws Exception {
        boolean administrator = JwtUtils.getAdministrator();
        return platformGbChannelService.findDeviceChannelList(administrator);
    }

    /**
     * 国标级联绑定的通道key集合
     */
    @PostMapping("channel_bind_key")
    public RestResult<?> findChannelBindKey(@Validated @RequestBody PlatformGbChannelParam param){
        boolean administrator = JwtUtils.getAdministrator();
        return platformGbChannelService.findChannelBindKey(param,administrator);
    }

    /**
     * 向上级平台添加国标通道
     * @param param
     * @return
     */
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param){
        boolean administrator = JwtUtils.getAdministrator();
        return platformGbChannelService.insert(param,administrator);
    }

    /**
     * 从上级平台移除国标通道
     * @param param
     * @return
     */
    @PostMapping("delete")
    public RestResult<?> delete(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param){
        boolean administrator = JwtUtils.getAdministrator();
        return platformGbChannelService.delete(param,administrator);
    }

}
