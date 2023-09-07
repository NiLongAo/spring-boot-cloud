package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/department",configuration = FeignConfiguration.class)
public interface DepartmentServiceFeign {

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    RestResult<?> select(@RequestParam(value = "departmentIdList",required = false) List<Long> departmentIdList, @RequestParam(value = "departmentName",required = false)String departmentName, @RequestParam("limit") Integer limit);


    @RequestMapping(value = "page", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody DepartmentParam userPageModel);

    @RequestMapping(value = "all", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> findAll();

    @RequestMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated DepartmentParam param);

    @RequestMapping(value = "remove", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id")Long id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);

    @RequestMapping(value = "tree", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> tree(@RequestBody @Validated DepartmentParam param);

}
