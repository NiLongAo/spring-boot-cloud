package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 国标流相关接口
 */
@FeignClient(value = "video-server",contextId = "video-server",path = "/api/media/server",configuration = FeignConfiguration.class)
public interface MediaServerServiceFeign {


    /**
     * 分页
     */
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody MediaServerPageParam param);

    /**
     * 详情
     */
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") String id);

    /**
     * 保存
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    RestResult<?> save(@Validated @RequestBody MediaServerSaveParam param);


    /**
     * 移除
     */
    @RequestMapping(value = "/remove",method = RequestMethod.DELETE)
    RestResult<?> remove(@RequestParam("id") String id);

    /**
     * 根据应用名和流id获取播放地址
     * @param deviceId 设备编号
     * @param channelId 通道编号
     */
    @RequestMapping(value = "/find_play_url",method = RequestMethod.GET)
    RestResult<?> findPlayUrl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId);
}
