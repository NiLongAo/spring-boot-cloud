package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.DictionaryItemService;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("ApiConfigDictionaryItemController")
@RequestMapping(value = "/api/config/dictionary_item")
public class DictionaryItemController extends ApiController {

    @Autowired
    DictionaryItemService dictionaryItemService;

    /**
     * 获取字典条目集合
     * @return
     */
    @GetMapping("find_item_list")
    @ResponseBody()
    public RestResult<?> findItemList(@RequestParam("typeId")  String typeId){
        List<NotNullMap> data = new ArrayList<>();
        List<DictionaryItem> dictionaryTypeList = dictionaryItemService.list(new LambdaQueryWrapper<DictionaryItem>().eq(DictionaryItem::getTypeId,typeId).orderByDesc(DictionaryItem::getSort));
        dictionaryTypeList.forEach(obj->{
            NotNullMap map = new NotNullMap();
            map.putString("itemId",obj.getId());
            map.putInteger("sort",obj.getSort());
            map.putString("value",obj.getValue());
            map.putString("name",obj.getName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(),null,data);
    }

    /**
     * 获取有效字典项
     * @return
     */
    @GetMapping("find_dict")
    @ResponseBody
    public RestResult<?> findDict(){
        return dictionaryItemService.findDict();
    }

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @PostMapping("page")
    @ResponseBody
    public PageResult page(@Validated @RequestBody DictionaryItemParam userPageModel){
        return dictionaryItemService.page(userPageModel);
    }

    /**
     * 保存字典条目
     * @param params
     * @return
     */
    @PostMapping("save")
    @ResponseBody
    public RestResult<?> save(@RequestBody @Validated DictionaryItemParam params){
        return   dictionaryItemService.save(params.id,params.name,params.num,params.typeId,params.value);
    }

    /**
     * 删除字典条目
     * @param id
     * @return
     */
    @GetMapping("remove")
    @ResponseBody
    public RestResult<?> remove(@RequestParam("id") String id){
        dictionaryItemService.remove(new QueryWrapper<DictionaryItem>().eq("id", id));
        return  RestResult.result(RespCode.CODE_0.getValue(),"删除成功");
    }

    @GetMapping("detail")
    @ResponseBody
    public RestResult<?> detail(@RequestParam("id") String id){
        DictionaryItem dictionaryItem = dictionaryItemService.getById(id);
        return  RestResult.result(RespCode.CODE_0.getValue(),null,dictionaryItem);
    }

}
