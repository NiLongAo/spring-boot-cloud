package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/role",configuration = FeignConfiguration.class)
public interface RoleServiceFeign {


    @RequestMapping(value = "/select",method = RequestMethod.GET)
    RestResult<?> select(@RequestParam(value = "roleIdList",required = false) List<Long> roleIdList, @RequestParam(value = "roleName",required = false)String roleName, @RequestParam("limit") Integer limit);

    @RequestMapping(value = "/page", consumes = "application/json",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody RoleParam userPageModel);

    @RequestMapping(value = "/all", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findAll();

    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody RoleParam param);

    @RequestMapping(value = "/remove", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id") Long id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);
}
