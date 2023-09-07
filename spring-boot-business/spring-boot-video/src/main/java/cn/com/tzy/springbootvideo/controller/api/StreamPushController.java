package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.StreamPushPageParam;
import cn.com.tzy.springbootentity.param.video.StreamPushSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.StreamPushService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 推流相关信息接口
 */
@RestController("ApiStreamPushController")
@RequestMapping(value = "/api/stream/push")
public class StreamPushController extends ApiController {

    @Resource
    private StreamPushService streamPushService;

    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody StreamPushPageParam param){
        return streamPushService.findPage(param);
    }

    /**
     * 详情
     */
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return streamPushService.detail(id);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody StreamPushSaveParam param){
        return streamPushService.save(param);
    }

    /**
     * 移除
     */
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id){
        return streamPushService.remove(id);
    }

    /**
     * 获取推流播放地址
     */
    @GetMapping( "get_play_url")
    public RestResult<?> getPlayUrl(@RequestParam("id") Long id){
        return streamPushService.getPlayUrl(id);
    }

}
