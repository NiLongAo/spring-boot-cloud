package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/tenant",configuration = FeignConfiguration.class)
public interface TenantServiceFeign {

    @RequestMapping(value = "/select", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> tenantSelect(@RequestParam(value = "tenantIdList",required = false) List<Long> tenantIdList,
                            @RequestParam(value = "tenantName",required = false)String tenantName,
                            @RequestParam("limit") Integer limit
    );

    @RequestMapping(value = "page", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    PageResult page( @RequestBody TenantParam param);

    @RequestMapping(value = "insert", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> insert(@RequestBody TenantUserVo param);

    @RequestMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> update(@RequestBody TenantParam param);

    @RequestMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam(value = "id") Long id);

    @RequestMapping(value = "/detail", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam(value = "id") Long id);

}
