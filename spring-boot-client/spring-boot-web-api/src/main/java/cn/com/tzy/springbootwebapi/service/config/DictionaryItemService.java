package cn.com.tzy.springbootwebapi.service.config;

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
     *
     * @return
     */
    public RestResult<?> findItemList(String typeId) {
        return dictionaryItemServiceFeign.findItemList(typeId);
    }

    /**
     * 根据用户账号获取用户信息
     *
     * @return
     */
    public PageResult page(DictionaryItemParam userPageModel) {
        return dictionaryItemServiceFeign.page(userPageModel);
    }

    /**
     * 保存字典条目
     *
     * @param params
     * @return
     */
    public RestResult<?> save(DictionaryItemParam params) {
        return dictionaryItemServiceFeign.save(params);
    }

    /**
     * 删除字典条目
     *
     * @param id
     * @return
     */
    public RestResult<?> remove(String id) {
        return dictionaryItemServiceFeign.remove(id);
    }

    public RestResult<?> detail(String id) {
        return dictionaryItemServiceFeign.detail(id);
    }

    public RestResult<?> findDict() {
        return dictionaryItemServiceFeign.findDict();
    }

}
