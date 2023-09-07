package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.*;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectPrivilege;
import cn.com.tzy.springbootentity.dome.bean.PositionConnectPrivilege;
import cn.com.tzy.springbootentity.dome.bean.Privilege;
import cn.com.tzy.springbootentity.dome.bean.RoleConnectPrivilege;
import cn.com.tzy.springbootentity.param.bean.DepartmentConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PositionConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.RoleConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.sms.TenantConnectPrivilegeParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限信息
 */
@RestController("ApiBeanPrivilegeController")
@RequestMapping(value = "/api/bean/privilege")
public class PrivilegeController  extends ApiController {

    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    RoleConnectPrivilegeService roleConnectPrivilegeService;
    @Autowired
    DepartmentConnectPrivilegeService departmentConnectPrivilegeService;
    @Autowired
    PositionConnectPrivilegeService positionConnectPrivilegeService;
    @Autowired
    TenantConnectPrivilegeService tenantConnectPrivilegeService;


    @GetMapping("init")
    @ResponseBody
    public RestResult<?> init(){
        if(privilegeService.init()){
            return RestResult.result(RespCode.CODE_0);
        }
       return RestResult.result(RespCode.CODE_2);
    }
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        List<NotNullMap> data = new ArrayList<>();
        List<Privilege> list = privilegeService.list();
        list.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putString("privilegeId",obj.getId());
            map.putString("privilegeName",obj.getPrivilegeName());
            map.putString("requestUrl",obj.getRequestUrl());
            map.putString("menuId",obj.getMenuId());
            map.putString("memo",obj.getMemo());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated PrivilegeParam param){
        return privilegeService.save(param.id,param.menuId,param.privilegeName,param.requestUrl,param.memo);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id){
        Privilege obj = privilegeService.getById(id);
        if(obj == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到权限信息，请联系管理员");
        }
        roleConnectPrivilegeService.remove(new LambdaQueryWrapper<RoleConnectPrivilege>().eq(RoleConnectPrivilege::getPrivilegeId,obj.getId()));
        departmentConnectPrivilegeService.remove(new LambdaQueryWrapper<DepartmentConnectPrivilege>().eq(DepartmentConnectPrivilege::getPrivilegeId,obj.getId()));
        positionConnectPrivilegeService.remove(new LambdaQueryWrapper<PositionConnectPrivilege>().eq(PositionConnectPrivilege::getPrivilegeId,obj.getId()));
        privilegeService.removeById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        Privilege p = privilegeService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,p);
    }


    @GetMapping("department_privilege_list")
    @ResponseBody
    public RestResult<?> findDepartmentPrivilegeList(@RequestParam("departmentId") Long departmentId){
        return departmentConnectPrivilegeService.findDepartmentPrivilegeList(departmentId);
    }

    @PostMapping("department_privilege_save")
    @ResponseBody
    public RestResult<?> departmentPrivilegeSave(@RequestBody DepartmentConnectPrivilegeParam save){
        return departmentConnectPrivilegeService.save(save.departmentId,save.privilegeList);
    }

    @GetMapping("position_privilege_list")
    @ResponseBody
    public RestResult<?> findPositionPrivilegeList(@RequestParam("positionId") Long positionId){
        return positionConnectPrivilegeService.findPositionPrivilegeList(positionId);
    }

    @PostMapping("position_privilege_save")
    @ResponseBody
    public RestResult<?> positionPrivilegeSave(@RequestBody PositionConnectPrivilegeParam save){
        return positionConnectPrivilegeService.save(save.positionId,save.privilegeList);
    }

    @GetMapping("role_privilege_list")
    @ResponseBody
    public RestResult<?> findRolePrivilegeList(@RequestParam("roleId") Long roleId){
        return roleConnectPrivilegeService.findRolePrivilegeList(roleId);
    }

    @PostMapping("role_privilege_save")
    @ResponseBody
    public RestResult<?> rolePrivilegeSave(@RequestBody RoleConnectPrivilegeParam save){
        return roleConnectPrivilegeService.save(save.roleId,save.privilegeList);
    }

    @GetMapping("tenant_privilege_list")
    @ResponseBody
    public RestResult<?> findTenantPrivilegeList(@RequestParam("tenantId") Long tenantId){
        return tenantConnectPrivilegeService.findPositionPrivilegeList(tenantId);
    }

    @PostMapping("tenant_privilege_save")
    @ResponseBody
    public RestResult<?> tenantPrivilegeSave(@RequestBody TenantConnectPrivilegeParam save){
        return tenantConnectPrivilegeService.save(save.tenantId,save.privilegeList);
    }
}
