package cn.com.tzy.springbootsms.controller.api.sms;

import cn.com.tzy.springbootsms.service.QuartzService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("JobController")
@RequestMapping(value = "/api/sms/quartz")
public class QuartzController extends ApiController {

    @Autowired
    QuartzService quartzService;


}
