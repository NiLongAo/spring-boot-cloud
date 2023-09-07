package cn.com.tzy.springbootsms.controller.api.sms;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sms.MobileMessageParam;
import cn.com.tzy.springbootsms.service.MobileMessageService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("ApiSmsMobileMessageController")
@RequestMapping(value = "/api/sms/mobile_message")
public class MobileMessageController  extends ApiController {

    @Autowired
    MobileMessageService mobileMessageService;


    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MobileMessageParam param){
        return mobileMessageService.findPage(param);
    }



    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return  mobileMessageService.detail(id);
    }

}
