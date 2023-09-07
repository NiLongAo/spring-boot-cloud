package cn.com.tzy.springbootapp.controller.sms;

import cn.com.tzy.springbootapp.service.sms.SmsSendService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "短信发送相关接口")
@RestController("AppSmsSmsSendController")
@RequestMapping(value = "/app/sms/sms_send")
public class SmsSendController extends ApiController {

    @Autowired
    private SmsSendService smsSendService;

    @ApiOperation(value = "发送短信", notes = "发送短信")
    @PostMapping("send")
    @ResponseBody
    public RestResult<?> send(@Validated @RequestBody SendParam param) throws IOException {
       return smsSendService.send(param);
    }

}
