package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/dictionary_item",configuration = FeignConfiguration.class)
public interface DictionaryItemServiceFeign {

    /**
     * 获取字典条目集合
     * @return
     */
    @RequestMapping(value = "/find_item_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findItemList(@RequestParam("typeId")  String typeId);

    /**
     * 根据用户账号获取用户信息
     * @return
     */
    @RequestMapping(value = "/page", consumes = "application/json",method = RequestMethod.POST)
    PageResult page(@Validated @RequestBody DictionaryItemParam userPageModel);

    /**
     * 保存字典条目
     * @param params
     * @return
     */
    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated DictionaryItemParam params);

    /**
     * 删除字典条目
     * @param id
     * @return
     */
    @RequestMapping(value = "/remove", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id") String id);

    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") String id);

    @RequestMapping(value = "/find_dict", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findDict();
}
