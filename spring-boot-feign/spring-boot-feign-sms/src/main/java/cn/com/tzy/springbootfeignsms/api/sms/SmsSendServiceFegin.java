package cn.com.tzy.springbootfeignsms.api.sms;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.SendParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "sms-server",contextId = "sms-server",path = "/api/sms/sms_send",configuration = FeignConfiguration.class)
public interface SmsSendServiceFegin {

    @RequestMapping(value = "/send",method = RequestMethod.POST)
    public RestResult<?> send(@Validated @RequestBody SendParam param);

}
