package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbChannelSaveParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 上级平台国标通道相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/platform/gb_channel",configuration = FeignConfiguration.class)
public interface PlatformGbChannelServiceFeign {


    /**
     * 分页（上级平台通道分页）
     */
    @RequestMapping(value = "/device_channel_list",method = RequestMethod.GET)
    RestResult<?> findDeviceChannelList();

    /**
     * 国标级联绑定的通道key集合
     * @param param
     * @return
     */
    @RequestMapping(value = "/channel_bind_key",method = RequestMethod.POST)
    RestResult<?> findChannelBindKey(@Validated @RequestBody PlatformGbChannelParam param);

    /**
     * 向上级平台添加国标通道
     * @param param
     * @return
     */
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param);


    /**
     * 从上级平台移除国标通道
     * @param param
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    RestResult<?> delete(@Validated({BaseModel.add.class}) @RequestBody PlatformGbChannelSaveParam param);

}
