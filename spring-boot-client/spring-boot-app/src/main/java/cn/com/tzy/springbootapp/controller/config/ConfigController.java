package cn.com.tzy.springbootapp.controller.config;

import cn.com.tzy.springbootapp.service.config.ConfigService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "系统配置接口")
@RestController("AppConfigConfigController")
@RequestMapping(value = "/app/config/config")
public class ConfigController extends ApiController {


    @Autowired
    ConfigService configService;

    @ApiOperation(value = "查询所有系统配置信息", notes = "查询所有系统配置信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> all(){
        return configService.all();
    }

    @ApiOperation(value = "根据系统配置编号查询详情", notes = "根据系统配置编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="k", value="系统配置编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("k") String k){
        return configService.detail(k);
    }

}
