package cn.com.tzy.springbootapp.service.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import cn.com.tzy.springbootfeignbean.api.sys.DictionaryItemServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryItemService {

    @Autowired
    DictionaryItemServiceFeign dictionaryItemServiceFeign;

    /**
     * 获取字典条目集合
     * @return
     */
    public RestResult<?> findItemList(String typeId){return dictionaryItemServiceFeign.findItemList(typeId);}

    public RestResult<?> findDict() {
        return dictionaryItemServiceFeign.findDict();
    }
}
