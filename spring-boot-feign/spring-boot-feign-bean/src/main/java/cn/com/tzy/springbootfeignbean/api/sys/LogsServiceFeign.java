package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/logs",configuration = FeignConfiguration.class)
public interface LogsServiceFeign {

    @RequestMapping(value = "page", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    PageResult page(@RequestBody LogsParam param);

    @RequestMapping(value = "/detail", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam(value = "id") Long id);

    @RequestMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> insert(@RequestBody LogsParam params);

}
