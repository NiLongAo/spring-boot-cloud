package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.ConfigService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Config;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("ApiConfigConfigController")
@RequestMapping(value = "/api/config/config")
public class ConfigController extends ApiController {

    @Resource
    private ConfigService configService;

    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        return configService.findAll();
    }

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody ConfigParam userPageModel){
        return configService.page(userPageModel);
    }



    @PostMapping("update")
    @ResponseBody()
    public RestResult<?> update(@RequestBody @Validated ConfigParam params){
        Config config = configService.find(params.k);
        if(config == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到系统配置信息");
        }
        config.setConfigName(params.configName);
        config.setV(params.v);
        configService.update(config);
        return RestResult.result(RespCode.CODE_0.getValue(),"修改成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("k") String k){
        return  RestResult.result(RespCode.CODE_0.getValue(),null,configService.find(k));
    }



}
