package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 推流相关信息接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/stream/push",configuration = FeignConfiguration.class)
public interface StreamPushServiceFeign {
    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody StreamPushPageParam param);

    /**
     * 详情
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);


    /**
     * 保存
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    RestResult<?> save(@Validated @RequestBody StreamPushSaveParam param);


    /**
     * 移除
     */
    @RequestMapping(value = "/remove",method = RequestMethod.DELETE)
    RestResult<?> remove(@RequestParam("id") Long id);

    /**
     * 获取推流播放地址
     */
    @RequestMapping(value = "/get_play_url",method = RequestMethod.GET)
    RestResult<?> getPlayUrl(@RequestParam("id") Long id);

}
