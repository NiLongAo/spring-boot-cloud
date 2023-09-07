package cn.com.tzy.springbootwebapi.controller.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.bean.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "菜单相关接口",position = 1)
@Controller("WebApiBeanMenuController")
@RequestMapping("/webapi/bean/menu")
public class MenuController extends ApiController {

    @Autowired
    MenuService menuService;

    @ApiOperation(value = "查询当前用户菜单树", notes = "查询当前用户菜单树")
    @PostMapping("user_tree_menu")
    @ResponseBody
    public RestResult<?> findUserTreeMenu() {
        return menuService.findUserTreeMenu();
    }

    @ApiOperation(value = "查询全部菜单树", notes = "查询全部菜单树")
    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@RequestBody @Validated MenuParam param){
        return menuService.tree(param);
    }

    @ApiOperation(value = "菜单分页查询", notes = "菜单分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody MenuParam userPageModel){
        return menuService.page(userPageModel);
    }


    @ApiOperation(value = "查询所有菜单信息", notes = "查询所有菜单信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
       return menuService.findAll();
    }

    @ApiOperation(value = "查询所有菜单及权限合成树", notes = "查询所有菜单及权限合成树")
    @GetMapping("menu_privilege_tree")
    @ResponseBody
    public RestResult<?> menuPrivilegeTree(){
        return menuService.menuPrivilegeTree();
    }

    @ApiOperation(value = "查询租户所有菜单及权限合成树", notes = "查询租户所有菜单及权限合成树")
    @GetMapping("tenant_menu_privilege_tree")
    @ResponseBody
    public RestResult<?> tenantMenuPrivilegeTree(@RequestParam("tenantId") Long tenantId){
        return menuService.tenantMenuPrivilegeTree(tenantId);
    }

    @ApiOperation(value = "保存菜单信息", notes = "保存菜单信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated MenuParam param){
        return menuService.save(param);
    }

    @ApiOperation(value = "根据菜单信息编号删除", notes = "根据菜单信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="菜单信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
       return menuService.remove(id);
    }

    @ApiOperation(value = "根据菜单信息编号查询详情", notes = "根据菜单信息编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="菜单信息编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        return menuService.detail(id);
    }


}
