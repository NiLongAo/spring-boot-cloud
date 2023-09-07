package cn.com.tzy.springbootwebapi.controller.sms;

import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.sms.SmsSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "短信发送相关接口",position = 3)
@RestController("WebApiSmsSmsSendController")
@RequestMapping(value = "/webapi/sms/sms_send")
public class SmsSendController  extends ApiController {

    @Autowired
    SmsSendService smsSendService;

    @ApiOperation(value = "发送短信", notes = "发送短信")
    @PostMapping("send")
    @ResponseBody
    public RestResult<?> send(@Validated @RequestBody SendParam param) throws IOException {
       return smsSendService.send(param);
    }

}
