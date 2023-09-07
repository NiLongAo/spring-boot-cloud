package cn.com.tzy.springbootwebapi.controller.config;


import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.config.DictionaryItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "字典类型条目接口", position = 1)
@RestController("WebApiConfigDictionaryItemController")
@RequestMapping(value = "/webapi/config/dictionary_item")
public class DictionaryItemController extends ApiController {

    @Resource
    DictionaryItemService dictionaryItemService;

    @ApiOperation(value = "获取字典条目集合", notes = "获取字典条目集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "字典类型编号", required = true, paramType = "query", dataType = "String", defaultValue = "")
    })
    @GetMapping("find_item_list")
    @ResponseBody()
    public RestResult<?> findItemList(@RequestParam("typeId") String typeId) {
        return dictionaryItemService.findItemList(typeId);
    }

    @ApiOperation(value = "根据用户账号获取用户信息", notes = "根据用户账号获取用户信息")
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody DictionaryItemParam param) {
        return dictionaryItemService.page(param);
    }

    @ApiOperation(value = "保存字典条目", notes = "保存字典条目")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DictionaryItemParam params) {
        return dictionaryItemService.save(params);
    }

    @ApiOperation(value = "删除字典条目", notes = "删除字典条目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典条目编号", required = true, paramType = "query", dataType = "String", defaultValue = "")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id) {
        return dictionaryItemService.remove(id);
    }

    @ApiOperation(value = "根据字典条目编号获取详情", notes = "根据字典条目编号获取详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典条目编号", required = true, paramType = "query", dataType = "String", defaultValue = "")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id) {
        return dictionaryItemService.detail(id);
    }

    @ApiOperation(value = "获取有效字典条目", notes = "获取有效字典条目")
    @GetMapping("find_dict")
    @ResponseBody
    public RestResult<?> findDict() {
        return dictionaryItemService.findDict();
    }

}
