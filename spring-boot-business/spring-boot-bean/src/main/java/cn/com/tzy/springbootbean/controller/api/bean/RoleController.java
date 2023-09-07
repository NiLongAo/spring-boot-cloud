package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.RoleConnectPrivilegeService;
import cn.com.tzy.springbootbean.service.api.RoleService;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.dome.bean.RoleConnectPrivilege;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单信息
 */
@RestController("ApiBeanRoleController")
@RequestMapping(value = "/api/bean/role")
public class RoleController  extends ApiController {

    @Autowired
    RoleService roleService;
    @Autowired
    RoleConnectPrivilegeService roleConnectPrivilegeService;

    /**
     * 角色信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> roleSelect(@RequestParam(value = "roleIdList",required = false)List<Long> roleIdList,@RequestParam(value = "roleName",required = false)String roleName,@RequestParam("limit") Integer limit){
        return roleService.roleSelect(roleIdList,roleName,limit);
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody RoleParam userPageModel){
        return roleService.page(userPageModel);
    }

    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        List<NotNullMap> data = new ArrayList<>();
        List<Role> list = roleService.list();
        list.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putLong("roleId",obj.getId());
            map.putLong("tenantId",obj.getTenantId());
            map.putString("roleName",obj.getRoleName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid RoleParam param){
        return roleService.save(param.id,param.memo,param.roleName);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        roleConnectPrivilegeService.remove(new LambdaQueryWrapper<RoleConnectPrivilege>().eq(RoleConnectPrivilege::getRoleId,id));
        roleService.removeById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }


    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        Role role = roleService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,role);
    }





}
