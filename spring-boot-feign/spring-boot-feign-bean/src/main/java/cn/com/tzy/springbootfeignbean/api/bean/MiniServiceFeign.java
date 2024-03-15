package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Mini;
import cn.com.tzy.springbootentity.param.bean.MiniUserParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/mini",configuration = FeignConfiguration.class)
public interface MiniServiceFeign {


    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated Mini param);

    @RequestMapping(value = "/find_open_id", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findOpenId(@RequestParam("openId") String openId);

    @RequestMapping(value = "/find_web_user_id", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findWebUserId(@RequestParam("userId") Long userId);

    @RequestMapping(value = "/save_mini_user", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> saveMiniUser(@RequestBody @Validated MiniUserParam param);

    @RequestMapping(value = "/unbind_mini_web", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> unbindMiniWeb(@RequestBody @Validated MiniUserParam param);

}
