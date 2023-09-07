package cn.com.tzy.springbootsms.controller.api.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageTemplateParam;
import cn.com.tzy.springbootsms.service.MobileMessageTemplateService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("ApiSmsMobileMessageTemplateController")
@RequestMapping(value = "/api/sms/mobile_message_template")
public class MobileMessageTemplateController  extends ApiController {

    @Autowired
    MobileMessageTemplateService mobileMessageTemplateService;

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MobileMessageTemplateParam param){
        return mobileMessageTemplateService.findPage(param);
    }

    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid MobileMessageTemplateParam param){
        return mobileMessageTemplateService.insert(param);
    }

    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Valid MobileMessageTemplateParam param){
        return mobileMessageTemplateService.update(param);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        return  mobileMessageTemplateService.remove(id);
    }


    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  mobileMessageTemplateService.detail(id);
    }


}
