package cn.com.tzy.springbootwebapi.controller.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.config.LogsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "系统日志接口接口", position = 1)
@RestController("WebApiLogsController")
@RequestMapping(value = "/webapi/config/logs")
public class LogsController extends ApiController {


    @Resource
    private LogsService logsService;

    @ApiOperation(value = "系统日志分页查询", notes = "系统日志分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody LogsParam param) {
        return logsService.page(param);
    }

    @ApiOperation(value = "系统日志详情", notes = "系统日志详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="日志编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id) {
        return logsService.detail(id);
    }

}
