package cn.com.tzy.springbootwebapi.controller.config;


import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryTypeParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootwebapi.service.config.DictionaryTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "字典类型接口",position = 1)
@RestController("WebApiConfigDictionaryTypeController")
@RequestMapping(value = "/webapi/config/dictionary_type")
public class DictionaryTypeController extends ApiController {

    @Autowired
    DictionaryTypeService dictionaryTypeService;

    @ApiOperation(value = "获取字典类型集合", notes = "获取字典类型集合")
    @GetMapping("find_type_list")
    @ResponseBody
    public RestResult<?> findTypeList(){
        return   dictionaryTypeService.findTypeList();
    }


    @ApiOperation(value = "保存字典类型", notes = "保存字典类型")
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DictionaryTypeParam params){
      return   dictionaryTypeService.save(params);
    }

    @ApiOperation(value = "删除字典类型", notes = "删除字典类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="字典类型编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id){
        return   dictionaryTypeService.remove(id);
    }

    @ApiOperation(value = "根据字典类型编号获取详情", notes = "根据字典类型编号获取详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="字典类型编号", required=true, paramType="query", dataType="String", defaultValue="")
    })
    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        return dictionaryTypeService.detail(id);
    }


}
