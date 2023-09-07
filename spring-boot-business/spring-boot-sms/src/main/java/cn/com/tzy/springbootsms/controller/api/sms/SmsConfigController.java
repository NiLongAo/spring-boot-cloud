package cn.com.tzy.springbootsms.controller.api.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.SmsConfigParam;
import cn.com.tzy.springbootsms.service.SmsConfigService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("ApiSmsSmsConfigController")
@RequestMapping(value = "/api/sms/sms_config")
public class SmsConfigController  extends ApiController {

    @Autowired
    SmsConfigService smsConfigService;

    @GetMapping("all")
    public RestResult<?> findAll(){
        return  smsConfigService.findAll();
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody SmsConfigParam param){
        return smsConfigService.findPage(param);
    }

    @PostMapping("insert")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid SmsConfigParam param){
        return smsConfigService.insert(param);
    }

    @PostMapping("update")
    @ResponseBody
    public RestResult<?> update(@RequestBody @Valid SmsConfigParam param){
        return smsConfigService.update(param);
    }

    @GetMapping("remove")
    public RestResult<?> remove(@RequestParam("id") Long id){
        return  smsConfigService.remove(id);
    }


    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  smsConfigService.detail(id);
    }


}
