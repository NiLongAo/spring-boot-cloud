package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootvideo.service.GbStreamService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 国标流相关接口
 */
@Log4j2
@RestController("ApiGbStreamController")
@RequestMapping(value = "/api/gb/stream")
public class GbStreamController extends ApiController {

    @Resource
    private GbStreamService gbStreamService;

    /**
     * 分页
     */
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody GbStreamPageParam param){
        return gbStreamService.findPage(param);
    }



}
