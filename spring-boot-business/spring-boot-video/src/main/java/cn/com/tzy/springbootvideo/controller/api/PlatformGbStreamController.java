package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamParam;
import cn.com.tzy.springbootentity.param.video.PlatformGbStreamSaveParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 视频流关联到级联平台
 */
@Log4j2
@RestController("ApiPlatformGbStreamController")
@RequestMapping(value = "/api/platform/gb_stream")
public class PlatformGbStreamController extends ApiController {

    @Resource
    private PlatformGbStreamService platformGbStreamService;

    /**
     * 级联视频流列表
     */
    @GetMapping("gb_stream_list")
    public RestResult<?> findGbStreamList() throws Exception {
        return platformGbStreamService.findGbStreamList();
    }


    /**
     * 级联视频流关联列表
     */
    @PostMapping("stream_bind_key")
    public RestResult<?> findStreamBindKey(@Validated @RequestBody PlatformGbStreamParam param) throws Exception {
        return platformGbStreamService.findStreamBindKey(param);
    }

    /**
     * 添加关联平台国标流信息
     */
    @PostMapping("add")
    public RestResult<?> add(@Validated @RequestBody PlatformGbStreamSaveParam param){
        return platformGbStreamService.insert(param);
    }

    /**
     * 移除关联平台国标流信息
     */
    @PostMapping("del")
    public RestResult<?> del(@Validated @RequestBody PlatformGbStreamSaveParam param){
        return platformGbStreamService.delete(param);
    }

}
