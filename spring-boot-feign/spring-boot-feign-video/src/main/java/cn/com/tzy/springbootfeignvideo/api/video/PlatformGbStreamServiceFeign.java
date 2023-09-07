package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 视频流关联到级联平台
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/platform/gb_stream",configuration = FeignConfiguration.class)
public interface PlatformGbStreamServiceFeign {

    /**
     * 级联视频流列表
     */
    @RequestMapping(value = "/gb_stream_list",method = RequestMethod.GET)
    RestResult<?> findGbStreamList();

    /**
     * 级联视频流关联列表
     */
    @RequestMapping(value = "/stream_bind_key",method = RequestMethod.POST)
    RestResult<?> findStreamBindKey(@Validated @RequestBody PlatformGbStreamParam param);
    /**
     * 添加关联平台国标流信息
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    RestResult<?> add(@Validated @RequestBody PlatformGbStreamSaveParam param);


    /**
     * 移除关联平台国标流信息
     */
    @RequestMapping(value = "/del",method = RequestMethod.POST)
    RestResult<?> del(@Validated @RequestBody PlatformGbStreamSaveParam param);
}

