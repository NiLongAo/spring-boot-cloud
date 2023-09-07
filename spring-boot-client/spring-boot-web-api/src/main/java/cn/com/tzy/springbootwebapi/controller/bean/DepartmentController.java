package cn.com.tzy.springbootwebapi.controller.bean;


import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.bean.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "部门信息相关接口",position = 1)
@RestController("WebApiBeanDepartmentController")
@RequestMapping(value = "/webapi/bean/department")
public class DepartmentController extends ApiController {

    @Autowired
    DepartmentService departmentService;

    @ApiOperation(value = "部门信息下拉展示(动态搜索数据源)", notes = "部门信息下拉展示(动态搜索数据源)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="idList", value="查询编号", required=false, paramType="query", dataType="list", defaultValue=""),
            @ApiImplicitParam(name="name", value="名称", required=false, paramType="query", dataType="string", defaultValue=""),
            @ApiImplicitParam(name="limit", value="查询数量", required=true, paramType="query", dataType="int", example="0")
    })
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> select(@RequestParam(value = "idList",required = false) List<Long> idList, @RequestParam(value = "name",required = false)String name, @RequestParam("limit") Integer limit){
        return departmentService.select(idList,name,limit);
    }

    @ApiOperation(value = "部门信息分页查询", notes = "部门信息分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody DepartmentParam userPageModel){
        return departmentService.page(userPageModel);
    }

    @ApiOperation(value = "查询所有部门信息", notes = "查询所有部门信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
        return departmentService.findAll();
    }

    @ApiOperation(value = "查询部门信息树", notes = "查询部门信息树")
    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@RequestBody @Validated DepartmentParam param){
        return departmentService.tree(param);
    }

    @ApiOperation(value = "保存部门信息", notes = "保存部门信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DepartmentParam param){
        return departmentService.save(param);
    }

    @ApiOperation(value = "根据部门信息编号删除", notes = "根据部门信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="部门信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id")Long id){
        return departmentService.remove(id);
    }

    @ApiOperation(value = "根据部门信息编号查询详情", notes = "根据部门信息编号查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="部门信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return departmentService.detail(id);
    }



}
