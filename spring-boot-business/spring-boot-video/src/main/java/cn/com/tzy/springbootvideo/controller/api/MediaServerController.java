package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.MediaServerPageParam;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.exception.VideoException;
import cn.com.tzy.springbootvideo.service.MediaServerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 流媒体相关信息接口
 */
@RestController("ApiMediaServerController")
@RequestMapping(value = "/api/media/server")
public class MediaServerController extends ApiController {

    @Resource
    private MediaServerService mediaServerService;


    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody MediaServerPageParam param){
        return mediaServerService.findPage(param);
    }

    /**
     * 详情
     */
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") String id){
        return mediaServerService.detail(id);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    public RestResult<?> save(@Validated @RequestBody MediaServerSaveParam param) throws VideoException {
        return mediaServerService.save(param);
    }

    /**
     * 移除
     */
    @DeleteMapping("remove")
    public RestResult<?> remove(@RequestParam("id") String id) {
        return mediaServerService.remove(id);
    }

    /**
     * 根据应用名和流id获取播放地址
     * @param deviceId 设备编号
     * @param channelId 通道编号
     */
    @GetMapping("find_play_url")
    public RestResult<?> findPlayUrl(@RequestParam("deviceId") String deviceId,@RequestParam("channelId")String channelId){
        return mediaServerService.findPlayUrl(deviceId,channelId);
    }

}
