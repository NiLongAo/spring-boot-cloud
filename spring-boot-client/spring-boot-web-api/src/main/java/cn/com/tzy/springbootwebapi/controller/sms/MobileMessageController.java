package cn.com.tzy.springbootwebapi.controller.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.sms.MobileMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "短信消息相关接口",position = 3)
@RestController("WebApiSmsMobileMessageController")
@RequestMapping(value = "/webapi/sms/mobile_message")
public class MobileMessageController  extends ApiController {

    @Autowired
    MobileMessageService mobileMessageService;


    @ApiOperation(value = "短信消息分页查询", notes = "短信消息分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MobileMessageParam param){
        return mobileMessageService.page(param);
    }


    @ApiOperation(value = "根据短信消息编号查询详情", notes = "根据短信消息编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  mobileMessageService.detail(id);
    }

}
