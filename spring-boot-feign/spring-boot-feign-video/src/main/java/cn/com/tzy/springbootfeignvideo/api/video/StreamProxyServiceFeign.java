package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 拉流相关信息接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/stream/proxy",configuration = FeignConfiguration.class)
public interface StreamProxyServiceFeign {

    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody StreamProxyPageParam param);

    /**
     * 详情
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    RestResult<?> save(@Validated @RequestBody StreamProxySaveParam param);

    /**
     * 移除
     */
    @RequestMapping(value = "/remove",method = RequestMethod.DELETE)
    RestResult<?> remove(@RequestParam("id") Long id);

    /**
     * 获取流媒体中ffmpeg.cmd模板
     */
    @RequestMapping(value = "/find_ffmpeg_cmd",method = RequestMethod.GET)
    RestResult<?> findFfmpegCmd(@RequestParam("mediaServerId")String mediaServerId);

    /**
     * 启用代理
     */
    @RequestMapping(value = "/start",method = RequestMethod.GET)
    RestResult<?> start(@RequestParam("id") Long id);

    /**
     * 停用代理
     */
    @RequestMapping(value = "/stop",method = RequestMethod.GET)
    RestResult<?> stop(@RequestParam("id") Long id);

    /**
     * 获取推流播放地址
     */
    @RequestMapping(value = "/get_play_url",method = RequestMethod.GET)
    RestResult<?> getPlayUrl(@RequestParam("id") Long id);
}
