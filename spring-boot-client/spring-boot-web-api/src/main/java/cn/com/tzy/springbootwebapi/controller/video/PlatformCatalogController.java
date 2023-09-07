package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.video.PlatformCatalogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "上级平台目录相关接口",position = 4)
@RestController("WebApiVideoPlatformCatalogController")
@RequestMapping(value = "/webapi/video/platform/catalog")
public class PlatformCatalogController extends ApiController {

    @Resource
    private PlatformCatalogService platformCatalogService;

    @ApiOperation(value = "获取目录树", notes = "获取目录树")
    @ApiImplicitParams({
            @ApiImplicitParam(name="platformId", value="上级平台编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @GetMapping("/tree")
    public RestResult<?> tree(@RequestParam("platformId") String platformId){
        return platformCatalogService.tree(platformId);
    }

    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody PlatformCatalog param){
        return platformCatalogService.insert(param);
    }

    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("update")
    public RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody PlatformCatalog param){
        return platformCatalogService.update(param);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="目录编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @DeleteMapping("delete")
    public RestResult<?> delete(@RequestParam("id") String id){
        return platformCatalogService.delete(id);
    }

    @ApiOperation(value = "删除关联", notes = "删除关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="目录编号", required=true, paramType="query", dataType="String", defaultValue=""),
            @ApiImplicitParam(name="type", value="类型 1.国标流 2.国标通道", required=true, paramType="query", dataType="Integer", example="0"),
    })
    @DeleteMapping("delete_relation")
    public RestResult<?> deleteRelation(@RequestParam("id") String id,@RequestParam("type") Integer type){
        return platformCatalogService.deleteRelation(id,type);
    }

}
