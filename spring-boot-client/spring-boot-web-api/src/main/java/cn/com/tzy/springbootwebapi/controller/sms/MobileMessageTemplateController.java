package cn.com.tzy.springbootwebapi.controller.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.sms.MobileMessageTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "短信模板相关接口",position = 3)
@RestController("WebApiSmsMobileMessageTemplateController")
@RequestMapping(value = "/webapi/sms/mobile_message_template")
public class MobileMessageTemplateController  extends ApiController {

    @Autowired
    MobileMessageTemplateService mobileMessageTemplateService;

    @ApiOperation(value = "短信模板分页查询", notes = "短信模板分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MobileMessageTemplateParam param){
        return mobileMessageTemplateService.page(param);
    }

    @ApiOperation(value = "新增短信模板", notes = "新增短信模板")
    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid MobileMessageTemplateParam param){
        return mobileMessageTemplateService.insert(param);
    }

    @ApiOperation(value = "修改短信模板", notes = "修改短信模板")
    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Valid MobileMessageTemplateParam param){
        return mobileMessageTemplateService.update(param);
    }

    @ApiOperation(value = "根据短信模板编号删除", notes = "根据短信模板编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        return  mobileMessageTemplateService.remove(id);
    }

    @ApiOperation(value = "根据短信模板编号查询详情", notes = "根据短信模板编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  mobileMessageTemplateService.detail(id);
    }


}
