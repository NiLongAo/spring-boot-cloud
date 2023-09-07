package cn.com.tzy.springbootwebapi.controller.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.PositionParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.bean.PositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "职位信息相关接口",position = 1)
@RestController("WebApiBeanPositionController")
@RequestMapping(value = "/webapi/bean/position")
public class PositionController extends ApiController {

    @Autowired
    PositionService positionService;

    @ApiOperation(value = "职位分页查询", notes = "职位分页查询")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody PositionParam param){
        return positionService.page(param);
    }

    @ApiOperation(value = "职位信息下拉展示(动态搜索数据源)", notes = "权限信息下拉展示(动态搜索数据源)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="idList", value="查询编号", required=false, paramType="query", dataType="list", defaultValue=""),
            @ApiImplicitParam(name="name", value="名称", required=false, paramType="query", dataType="string", defaultValue=""),
            @ApiImplicitParam(name="limit", value="查询数量", required=true, paramType="query", dataType="int", example="0")
    })
    @GetMapping("select")
    @ResponseBody
    public RestResult<?> select(@RequestParam(value = "idList",required = false) List<Long> idList, @RequestParam(value = "name",required = false)String name, @RequestParam("limit") Integer limit){
        return positionService.select(idList,name,limit);
    }


    @ApiOperation(value = "查询职位树", notes = "查询职位树")
    @PostMapping("tree")
    @ResponseBody
    public RestResult<?> tree(@RequestBody @Validated PositionParam param){
        return positionService.tree(param);
    }

    @ApiOperation(value = "查询所有职位信息", notes = "查询所有职位信息")
    @GetMapping("all")
    @ResponseBody
    public RestResult<?> findAll(){
       return positionService.findAll();
    }

    @ApiOperation(value = "保存职位信息", notes = "保存职位信息")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated PositionParam param){
        return positionService.save(param);
    }

    @ApiOperation(value = "根据职位信息编号删除", notes = "根据职位信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="职位信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") Long id){
       return positionService.remove(id);
    }

    @ApiOperation(value = "根据职位信息编号删除", notes = "根据职位信息编号删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="职位信息编号", required=true, paramType="query", dataType="Long", example="0")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") Long id){
        return positionService.detail(id);
    }
}
