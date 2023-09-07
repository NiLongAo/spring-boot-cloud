package cn.com.tzy.springbootwebapi.controller.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.sms.SmsConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "短信配置相关接口",position = 3)
@RestController("WebApiSmsSmsConfigController")
@RequestMapping(value = "/webapi/sms/sms_config")
public class SmsConfigController  extends ApiController {

    @Autowired
    SmsConfigService smsConfigService;

    @ApiOperation(value = "查询所有短信配置信息", notes = "查询所有短信配置信息")
    @GetMapping("all")
    public RestResult<?> all(){
        return  smsConfigService.all();
    }

    @ApiOperation(value = "短信配置分页查询", notes = "短信配置分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody SmsConfigParam param){
        return smsConfigService.page(param);
    }

    @ApiOperation(value = "新增短信配置", notes = "新增短信配置")
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid SmsConfigParam param){
        return smsConfigService.insert(param);
    }

    @ApiOperation(value = "修改短信配置", notes = "修改短信配置")
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Valid SmsConfigParam param){
        return smsConfigService.update(param);
    }

    @ApiOperation(value = "根据短信配置编号删除", notes = "根据短信配置编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id){
        return  smsConfigService.remove(id);
    }

    @ApiOperation(value = "根据短信配置编号查询详情", notes = "根据短信配置编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  smsConfigService.detail(id);
    }


}
