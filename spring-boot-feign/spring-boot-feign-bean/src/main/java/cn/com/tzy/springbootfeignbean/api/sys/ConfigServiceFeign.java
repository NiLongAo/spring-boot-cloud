package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/config",configuration = FeignConfiguration.class)
public interface ConfigServiceFeign {
    @RequestMapping(value = "/all", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> all();
    @RequestMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> update(@RequestBody ConfigParam param);
    @RequestMapping(value = "/page", consumes = "application/json",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody ConfigParam param);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("k") String k);
}
