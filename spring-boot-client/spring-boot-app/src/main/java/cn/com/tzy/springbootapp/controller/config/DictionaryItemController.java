package cn.com.tzy.springbootapp.controller.config;


import cn.com.tzy.springbootapp.service.config.DictionaryItemService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "字典类型条目接口")
@RestController("AppConfigDictionaryItemController")
@RequestMapping(value = "/app/config/dictionary_item")
public class DictionaryItemController extends ApiController {

    @Autowired
    private DictionaryItemService dictionaryItemService;

    @ApiOperation(value = "获取字典条目集合", notes = "获取字典条目集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name="typeId", value="字典类型编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("find_item_list")
    @ResponseBody()
    public RestResult<?> findItemList(@RequestParam("typeId") String typeId){
        return dictionaryItemService.findItemList(typeId);
    }

    @ApiOperation(value = "获取有效字典条目", notes = "获取有效字典条目")
    @GetMapping("find_dict")
    @ResponseBody
    public RestResult<?> findDict() {
        return dictionaryItemService.findDict();
    }

}
