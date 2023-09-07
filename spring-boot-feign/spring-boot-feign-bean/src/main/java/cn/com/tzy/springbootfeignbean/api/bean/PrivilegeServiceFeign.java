package cn.com.tzy.springbootfeignbean.api.bean;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PositionConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.RoleConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.sms.TenantConnectPrivilegeParam;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/bean/privilege",configuration = FeignConfiguration.class)
public interface PrivilegeServiceFeign {
    /**
     * 初始化权限缓存
     * @return
     */
    @RequestMapping(value = "/init", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> init ();

    @RequestMapping(value = "/all", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findAll();

    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated PrivilegeParam param);

    @RequestMapping(value = "/remove", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id") String id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") String id);

    @RequestMapping(value = "/department_privilege_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findDepartmentPrivilegeList(@RequestParam("departmentId") Long departmentId);

    @RequestMapping(value = "/department_privilege_save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> departmentPrivilegeSave(@RequestBody DepartmentConnectPrivilegeParam save);

    @RequestMapping(value = "/position_privilege_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findPositionPrivilegeList(@RequestParam("positionId") Long positionId);

    @RequestMapping(value = "/position_privilege_save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> positionPrivilegeSave(@RequestBody PositionConnectPrivilegeParam save);

    @RequestMapping(value = "/role_privilege_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findRolePrivilegeList(@RequestParam("roleId") Long roleId);

    @RequestMapping(value = "/role_privilege_save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> rolePrivilegeSave(@RequestBody RoleConnectPrivilegeParam save);

    @RequestMapping(value = "/tenant_privilege_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findTenantPrivilegeList(@RequestParam("tenantId") Long tenantId);

    @RequestMapping(value = "/tenant_privilege_save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> tenantPrivilegeSave(@RequestBody TenantConnectPrivilegeParam save);
}
