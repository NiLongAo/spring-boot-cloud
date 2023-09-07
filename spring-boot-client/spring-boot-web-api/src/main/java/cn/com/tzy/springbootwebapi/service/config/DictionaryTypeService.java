package cn.com.tzy.springbootwebapi.service.config;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryTypeParam;
import cn.com.tzy.springbootfeignbean.api.sys.DictionaryTypeServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryTypeService {

    @Autowired
    DictionaryTypeServiceFeign dictionaryTypeServiceFeign;

    /**
     * 获取字典类型集合
     * @return
     */
    public RestResult<?> findTypeList(){return dictionaryTypeServiceFeign.findTypeList();}

    /**
     * 保存字典类型
     * @param params
     * @return
     */
    public RestResult<?> save(DictionaryTypeParam params){return dictionaryTypeServiceFeign.save(params);}

    /**
     * 删除字典类型
     * @param id
     * @return
     */
    public RestResult<?> remove(String id){
        return dictionaryTypeServiceFeign.remove(id);
    }

    public RestResult<?> detail(String id) {
        return dictionaryTypeServiceFeign.detail(id);
    }
}
