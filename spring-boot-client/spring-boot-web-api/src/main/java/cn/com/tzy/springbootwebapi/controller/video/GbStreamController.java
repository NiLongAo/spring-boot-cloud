package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootentity.param.video.GbStreamPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.GbStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;



@Api(tags = "国标流相关接口",position = 4)
@RestController("WebApiVideoGbStreamController")
@RequestMapping(value = "/webapi/video/gb/stream")
public class GbStreamController extends ApiController {

    @Resource
    private GbStreamService gbStreamService;

    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody GbStreamPageParam param){
        return gbStreamService.page(param);
    }



}
