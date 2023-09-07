package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.DictionaryItemService;
import cn.com.tzy.springbootbean.service.api.DictionaryTypeService;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootentity.dome.sys.DictionaryType;
import cn.com.tzy.springbootentity.param.sys.DictionaryTypeParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("ApiConfigDictionaryTypeController")
@RequestMapping(value = "/api/config/dictionary_type")
public class DictionaryTypeController extends ApiController {

    @Autowired
    DictionaryItemService dictionaryItemService;
    @Autowired
    DictionaryTypeService dictionaryTypeService;

    /**
     * 获取字典类型集合
     * @return
     */
    @GetMapping("find_type_list")
    @ResponseBody
    public RestResult<?> findTypeList(){
        List<NotNullMap> data = new ArrayList<>();
        List<DictionaryType> dictionaryTypeList = dictionaryTypeService.list(new QueryWrapper<DictionaryType>().eq("status", ConstEnum.Flag.YES.getValue()));
        dictionaryTypeList.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putString("typeId",obj.getId());
            map.putString("code",obj.getCode());
            map.putInteger("status",obj.getStatus());
            map.putString("name",obj.getName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }



    /**
     * 保存字典类型
     * @param params
     * @return
     */
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DictionaryTypeParam params){
      return   dictionaryTypeService.save(params.id,params.code,params.name,params.status);
    }

    /**
     * 删除字典类型
     * @param id
     * @return
     */
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id){
        boolean type_id = dictionaryItemService.remove(new QueryWrapper<DictionaryItem>().eq("type_id", id));
        boolean b = dictionaryTypeService.remove(new QueryWrapper<DictionaryType>().eq("id", id));
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        DictionaryType entity = dictionaryTypeService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,entity);
    }
}
