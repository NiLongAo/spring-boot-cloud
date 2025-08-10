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
    RoleConnectMenuService roleConnectMenuService;
    @Autowired
    DepartmentConnectMenuService departmentConnectMenuService;
    @Autowired
    PositionConnectMenuService positionConnectMenuService;



    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@Validated @RequestBody MenuParam param){
        return menuService.tree(param.getTopName(),param.getIsShowPrivilege(),param.getMenuName());
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
            map.putInteger("level",obj.getType());
            map.putString("menuName",obj.getMenuName());
            map.putString("path",obj.getPath());
            map.putString("viewPath",obj.getComponent());
            map.putInteger("hideMenu",obj.getHideInMenu());
            map.putInteger("isOpen",obj.getStatus());
            map.putInteger("num",obj.getOrder());
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
        if(menu.getType()==Menu.MenuType.BUTTON.getType()){
            roleConnectMenuService.remove(new LambdaQueryWrapper<RoleConnectMenu>().eq(RoleConnectMenu::getMenuId,menu.getId()));
            departmentConnectMenuService.remove(new LambdaQueryWrapper<DepartmentConnectMenu>().eq(DepartmentConnectMenu::getMenuId,menu.getId()));
            positionConnectMenuService.remove(new LambdaQueryWrapper<PositionConnectMenu>().eq(PositionConnectMenu::getMenuId,menu.getId()));
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

    /**
     * 根据职位Id获取用户集合
     */
    @GetMapping("init_bottom")
    @ResponseBody
    public RestResult<?> initBottom() {
        return menuService.initBottom();
    }

}
