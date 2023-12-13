package cn.com.tzy.springbootapp.controller.config;

import cn.com.tzy.springbootapp.service.config.AreaService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "地理信息相关接口", position = 1)
@RestController("AppConfigAreaController")
@RequestMapping(value = "/app/config/area")
public class AreaController extends ApiController {

    @Autowired
    private AreaService areaService;

    @ApiOperation(value = "查询所有经纬度及名称", notes = "查询所有经纬度及名称")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> all() {
        return areaService.all();
    }
}
