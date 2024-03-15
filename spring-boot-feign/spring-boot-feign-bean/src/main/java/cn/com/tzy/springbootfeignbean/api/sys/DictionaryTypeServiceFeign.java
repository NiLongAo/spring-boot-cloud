package cn.com.tzy.springbootfeignbean.api.sys;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryTypeParam;
import cn.com.tzy.springbootstarterfeign.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bean-server",contextId = "bean-server",path = "/api/config/dictionary_type",configuration = FeignConfiguration.class)
public interface DictionaryTypeServiceFeign {

    /**
     * 获取字典类型集合
     * @return
     */
    @RequestMapping(value = "/find_type_list", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> findTypeList();

    /**
     * 保存字典类型
     * @param params
     * @return
     */
    @RequestMapping(value = "/save", consumes = "application/json",method = RequestMethod.POST)
    RestResult<?> save(@RequestBody @Validated DictionaryTypeParam params);

    /**
     * 删除字典类型
     * @param id
     * @return
     */
    @RequestMapping(value = "/remove", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> remove(@RequestParam("id") String id);

    /**
     * 删除字典详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail", consumes = "application/json",method = RequestMethod.GET)
    RestResult<?> detail(@RequestParam("id") String id);
}
