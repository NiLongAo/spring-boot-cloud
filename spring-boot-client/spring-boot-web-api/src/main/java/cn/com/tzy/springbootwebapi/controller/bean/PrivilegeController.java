package cn.com.tzy.springbootwebapi.controller.bean;


import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PositionConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.RoleConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.sms.TenantConnectPrivilegeParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.bean.PrivilegeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "权限信息相关接口",position = 1)
@RestController("WebApiBeanPrivilegeController")
@RequestMapping(value = "/webapi/bean/privilege")
public class PrivilegeController extends ApiController {

    @Autowired
    PrivilegeService privilegeService;

    @ApiOperation(value = "初始化权限信息", notes = "初始化权限信息")
    @GetMapping("init")
    @ResponseBody
    public RestResult<?> init(){
       return privilegeService.init();
    }

    @ApiOperation(value = "查询所有权限信息", notes = "查询所有权限信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){

        return privilegeService.findAll();
    }

    @ApiOperation(value = "保存权限信息", notes = "保存权限信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated PrivilegeParam param){
        return privilegeService.save(param);
    }

    @ApiOperation(value = "根据权限信息编号删除", notes = "根据权限信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="权限信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id){
        return privilegeService.remove(id);
    }

    @ApiOperation(value = "根据权限信息编号删除", notes = "根据权限信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="权限信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        return privilegeService.detail(id);
    }

    @ApiOperation(value = "查询所有部门权限信息", notes = "查询所有部门权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="departmentId", value="部门编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("department_privilege_list")
    public RestResult<?> findDepartmentPrivilegeList(@RequestParam("departmentId") Long departmentId){
        return privilegeService.findDepartmentPrivilegeList(departmentId);
    }
    @ApiOperation(value = "保存部门权限信息", notes = "保存部门权限信息")
    @PostMapping("department_privilege_save")
    @ResponseBody
    public RestResult<?> departmentPrivilegeSave(@RequestBody DepartmentConnectPrivilegeParam save){
        return privilegeService.departmentPrivilegeSave(save);
    }

    @ApiOperation(value = "查询所有职位权限信息", notes = "查询所有职位权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="positionId", value="职位编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("position_privilege_list")
    public RestResult<?> findPositionPrivilegeList(@RequestParam("positionId") Long positionId){
        return privilegeService.findPositionPrivilegeList(positionId);
    }

    @ApiOperation(value = "保存职位权限信息", notes = "保存职位权限信息")
    @PostMapping("position_privilege_save")
    @ResponseBody
    public RestResult<?> positionPrivilegeSave(@RequestBody PositionConnectPrivilegeParam save){
        return privilegeService.positionPrivilegeSave(save);
    }

    @ApiOperation(value = "查询所有角色权限信息", notes = "查询所有角色权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="roleId", value="角色编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("role_privilege_list")
    public RestResult<?> findRolePrivilegeList(@RequestParam("roleId") Long roleId){
        return privilegeService.findRolePrivilegeList(roleId);
    }

    @ApiOperation(value = "保存角色权限信息", notes = "保存角色权限信息")
    @PostMapping("role_privilege_save")
    @ResponseBody
    public RestResult<?> rolePrivilegeSave(@RequestBody RoleConnectPrivilegeParam save){
        return privilegeService.rolePrivilegeSave(save);
    }

    @ApiOperation(value = "查询所有租户权限信息", notes = "查询所有租户权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="roleId", value="角色编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("tenant_privilege_list")
    public RestResult<?> findTenantPrivilegeList(@RequestParam("tenantId") Long tenantId){
        return privilegeService.findTenantPrivilegeList(tenantId);
    }

    @ApiOperation(value = "保存租户权限信息", notes = "保存租户权限信息")
    @PostMapping("tenant_privilege_save")
    @ResponseBody
    public RestResult<?> tenantPrivilegeSave(@Validated({BaseModel.add.class}) @RequestBody TenantConnectPrivilegeParam save){
        return privilegeService.tenantPrivilegeSave(save);
    }
}
