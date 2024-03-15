package cn.com.tzy.springbootfeignsms.api.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sms-server",contextId = "sms-server",path = "/api/sms/mobile_message_template",configuration = FeignConfiguration.class)
public interface MobileMessageTemplateServiceFeign {


    @RequestMapping(value = "/page",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody MobileMessageTemplateParam param);

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    RestResult<?> insert(@Validated @RequestBody MobileMessageTemplateParam param);

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    RestResult<?> update(@Validated @RequestBody MobileMessageTemplateParam param);

    @RequestMapping(value = "/remove",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam(value = "id")Long id);

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam(value = "id")Long id);
}
