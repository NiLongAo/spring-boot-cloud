package cn.com.tzy.springbootsms.controller.api.sms;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootsms.config.sms.SmsSendManager;
import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("ApiSmsSmsSendController")
@RequestMapping(value = "/api/sms/sms_send")
public class SmsSendController  extends ApiController {

    @Autowired
    SmsSendManager smsSendManager;

    @PostMapping("send")
    @ResponseBody
    public RestResult<?> send(@Validated @RequestBody SendParam param){
       return smsSendManager.smsSend(param);
    }

}
