package cn.com.tzy.springbootapp.controller.video;

import cn.com.tzy.springbootcomm.common.model.BaseModel;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootentity.param.video.ParentPlatformPageParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootapp.service.video.ParentPlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "级联平台管理(上级平台)",position = 4)
@RestController("AppVideoParentPlatformController")
@RequestMapping(value = "/app/video/parent/platform")
public class ParentPlatformController extends ApiController {

    @Resource
    private ParentPlatformService parentPlatformService;

    @ApiOperation(value = "获取注册到本服务的所有sip服务", notes = "获取注册到本服务的所有sip服务")
    @GetMapping("sip_list")
    public RestResult<?> findSipList(){
        return parentPlatformService.findSipList();
    }

    @ApiOperation(value = "分页", notes = "分页")
    @PostMapping("page")
    public PageResult page(@Validated @RequestBody ParentPlatformPageParam param){
        return parentPlatformService.page(param);
    }

    @ApiOperation(value = "详情", notes = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="上级平台编号", required=true, paramType="query", dataType="long", example="0"),
    })
    @GetMapping("detail")
    public RestResult<?> detail(@RequestParam("id") Long id){
        return parentPlatformService.detail(id);
    }

    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("insert")
    public RestResult<?> insert(@Validated({BaseModel.add.class}) @RequestBody ParentPlatform param){
        return parentPlatformService.insert(param);
    }

    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("update")
    public RestResult<?> update(@Validated({BaseModel.edit.class}) @RequestBody ParentPlatform param){
        return parentPlatformService.update(param);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="上级平台编号", required=true, paramType="query", dataType="String", defaultValue=""),
    })
    @DeleteMapping("delete")
    public RestResult<?> delete(@RequestParam("id") Long id){
        return parentPlatformService.delete(id);
    }

}
