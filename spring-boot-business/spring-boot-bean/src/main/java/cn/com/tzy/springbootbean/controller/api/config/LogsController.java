package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.LogsService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("ApiConfigLogsController")
@RequestMapping(value = "/api/config/logs")
public class LogsController extends ApiController {

    @Resource
    private LogsService logsService;


    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody LogsParam param){
        return logsService.pages(param);
    }


    @GetMapping("/detail")
    public RestResult<?> detail(@RequestParam("id") Long id) {
        return logsService.detail(id);
    }


    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> insert(@RequestBody @Validated LogsParam params){
        return  logsService.insert(params);
    }
}
