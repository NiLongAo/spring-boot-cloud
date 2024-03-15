package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/position",configuration = FeignConfiguration.class)
public interface PositionServiceFeign {

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    RestResult<?> select(@RequestParam(value = "positionIdList",required = false) List<Long> positionIdList, @RequestParam(value = "positionName",required = false)String positionName, @RequestParam("limit") Integer limit);

    @RequestMapping(value = "tree", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> tree(@RequestBody @Validated PositionParam param);

    @RequestMapping(value = "/page", consumes = "application/json",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody PositionParam userPageModel);

    @RequestMapping(value = "/all", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findAll();

    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated PositionParam param);

    @RequestMapping(value = "/remove", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id") Long id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") Long id);
}
