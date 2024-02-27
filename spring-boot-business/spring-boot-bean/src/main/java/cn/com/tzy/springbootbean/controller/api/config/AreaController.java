package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.AreaService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("ApiConfigAreaController")
@RequestMapping(value = "/api/config/area")
public class AreaController  extends ApiController {

    @Autowired
    AreaService areaService;

    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAreaAll(){
        return areaService.findAreaAll();
    }


}
