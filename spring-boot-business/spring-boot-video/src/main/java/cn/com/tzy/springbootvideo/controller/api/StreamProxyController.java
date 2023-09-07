package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamProxyPageParam;
import cn.com.tzy.springbootentity.param.video.StreamProxySaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.StreamProxyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 拉流相关信息接口
 */
@RestController("ApiStreamProxyController")
@RequestMapping(value = "/api/stream/proxy")
public class StreamProxyController  extends ApiController {

    @Resource
    private StreamProxyService streamProxyService;

    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody StreamProxyPageParam param){
        return streamProxyService.findPage(param);
    }

    /**
     * 详情
     */
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return streamProxyService.detail(id);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody StreamProxySaveParam param){
        return streamProxyService.save(param);
    }

    /**
     * 移除
     */
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id) {
        return streamProxyService.remove(id);
    }

    /**
     * 获取流媒体中ffmpeg.cmd模板
     */
    @GetMapping("find_ffmpeg_cmd")
    public RestResult<?> findFfmpegCmd(@RequestParam("mediaServerId")String mediaServerId){
        return streamProxyService.findFfmpegCmd(mediaServerId);
    }

    /**
     * 启用代理
     */
    @GetMapping("start")
    public RestResult<?> start(@RequestParam("id") Long id){
        return streamProxyService.start(id);
    }
    /**
     * 停用代理
     */
    @GetMapping("stop")
    public RestResult<?> stop(@RequestParam("id") Long id){
        return streamProxyService.stop(id);
    }

    /**
     * 获取推流播放地址
     */
    @GetMapping( "get_play_url")
    public RestResult<?> getPlayUrl(@RequestParam("id") Long id){
        return streamProxyService.getPlayUrl(id);
    }

}
