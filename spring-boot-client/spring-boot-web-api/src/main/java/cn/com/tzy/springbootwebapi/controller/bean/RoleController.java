package cn.com.tzy.springbootwebapi.controller.bean;


import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.bean.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "角色信息相关接口",position = 1)
@RestController("WebApiBeanRoleController")
@RequestMapping(value = "/webapi/bean/role")
public class RoleController extends ApiController {

    @Autowired
    RoleService roleService;

    @ApiOperation(value = "角色分页查询", notes = "角色分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody RoleParam userPageModel){
        return roleService.page(userPageModel);
    }

    @ApiOperation(value = "角色信息下拉展示(动态搜索数据源)", notes = "角色信息下拉展示(动态搜索数据源)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="idList", value="查询编号", required=false, paramType="query", dataType="list", defaultValue=""),
            @ApiImplicitParam(name="name", value="名称", required=false, paramType="query", dataType="string", defaultValue=""),
            @ApiImplicitParam(name="limit", value="查询数量", required=true, paramType="query", dataType="int", example="0")
    })
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> select(@RequestParam(value = "idList",required = false) List<Long> idList, @RequestParam(value = "name",required = false)String name, @RequestParam("limit") Integer limit){
        return roleService.select(idList,name,limit);
    }

    @ApiOperation(value = "查询所有角色信息", notes = "查询所有角色信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){return roleService.findAll();
    }

    @ApiOperation(value = "保存角色信息", notes = "保存角色信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Valid RoleParam param){
        return roleService.save(param);
    }

    @ApiOperation(value = "根据角色信息编号删除", notes = "根据角色信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="角色信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
        return roleService.remove(id);
    }

    @ApiOperation(value = "根据角色信息编号删除", notes = "根据角色信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="角色信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return roleService.detail(id);
    }




}
