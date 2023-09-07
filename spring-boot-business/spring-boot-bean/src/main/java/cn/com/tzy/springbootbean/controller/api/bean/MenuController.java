package cn.com.tzy.springbootbean.controller.api.bean;

import cn.com.tzy.springbootbean.service.api.*;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.*;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单信息
 */
@RestController("ApiBeanMenuController")
@RequestMapping(value = "/api/bean/menu")
public class MenuController  extends ApiController {

    @Autowired
    MenuService menuService;
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    RoleConnectPrivilegeService roleConnectPrivilegeService;
    @Autowired
    DepartmentConnectPrivilegeService departmentConnectPrivilegeService;
    @Autowired
    PositionConnectPrivilegeService positionConnectPrivilegeService;



    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@Validated @RequestBody MenuParam param){
        return menuService.tree(param.topName,param.isShowPrivilege,param.menuName);
    }

    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MenuParam userPageModel){
        return menuService.page(userPageModel);
    }

    @GetMapping("menu_privilege_tree")
    @ResponseBody
    public RestResult<?> menuPrivilegeTree(){
        return menuService.menuPrivilegeTree();
    }

    @GetMapping("tenant_menu_privilege_tree")
    @ResponseBody
    public RestResult<?> tenantMenuPrivilegeTree(@RequestParam("tenantId") Long tenantId) throws Exception {
        return menuService.tenantMenuPrivilegeTree(tenantId);
    }


    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        List<NotNullMap> data = new ArrayList<>();
        List<Menu> list = menuService.list();
        list.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putString("parentId",obj.getParentId());
            map.putString("menuId",obj.getId());
            map.putInteger("level",obj.getLevel());
            map.putString("menuName",obj.getMenuName());
            map.putString("path",obj.getPath());
            map.putString("viewPath",obj.getViewPath());
            map.putInteger("hideMenu",obj.getHideMenu());
            map.putInteger("isOpen",obj.getIsOpen());
            map.putInteger("num",obj.getNum());
            map.putString("memo",obj.getMemo());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    /**
     * 获取用户菜单信息
     * @param userId 用户编号
     * @return
     * @throws Exception
     */
    @PostMapping("user_tree_menu")
    @ResponseBody
    public RestResult<?> findUserTreeMenu(@RequestParam("userId") Long userId) throws Exception {
        return menuService.findUserTreeMenu(userId);
    }

    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated MenuParam param){
        return menuService.save(param);
    }

    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        Menu menu = menuService.getOne(new LambdaQueryWrapper<Menu>().eq(Menu::getId, id));
        if(menu== null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取到菜单信息");
        }
        Menu parent = menuService.getOne(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId,menu.getId()));
        if(parent != null){
            return RestResult.result(RespCode.CODE_0.getValue(),"请先删除子级菜单");
        }
        List<Privilege> privilegeList = privilegeService.list(new LambdaQueryWrapper<Privilege>().eq(Privilege::getMenuId, menu.getId()));
        List<String> idList = new ArrayList<>();
        privilegeList.forEach(obj->{
            idList.add(obj.getId());
            roleConnectPrivilegeService.remove(new LambdaQueryWrapper<RoleConnectPrivilege>().eq(RoleConnectPrivilege::getPrivilegeId,obj.getId()));
            departmentConnectPrivilegeService.remove(new LambdaQueryWrapper<DepartmentConnectPrivilege>().eq(DepartmentConnectPrivilege::getPrivilegeId,obj.getId()));
            positionConnectPrivilegeService.remove(new LambdaQueryWrapper<PositionConnectPrivilege>().eq(PositionConnectPrivilege::getPrivilegeId,obj.getId()));
        });
        if(!idList.isEmpty()){
            privilegeService.removeByIds(idList);
        }
        menuService.removeById(menu.getId());
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        Menu menu = menuService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,menu);
    }

}
