package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/menu",configuration = FeignConfiguration.class)
public interface MenuServiceFeign {

    @RequestMapping(value = "tree", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> tree(@RequestBody @Validated MenuParam param);

    @RequestMapping(value = "/user_tree_menu", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> findUserTreeMenu(@RequestParam(value = "userId") Long userId);

    @RequestMapping(value = "/page", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody MenuParam userPageModel);

    @RequestMapping(value = "/all", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> findAll();

    @RequestMapping(value = "/menu_privilege_tree", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> menuPrivilegeTree();

    @RequestMapping(value = "/tenant_menu_privilege_tree", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    RestResult<?> tenantMenuPrivilegeTree(@RequestParam("tenantId") Long tenantId);

    @RequestMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated MenuParam param);

    @RequestMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    public RestResult<?> remove(@RequestParam("id")Long id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") String id);
}
