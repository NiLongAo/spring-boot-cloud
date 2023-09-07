package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import com.baomidou.mybatisplus.extension.service.IService;
public interface DictionaryItemService extends IService<DictionaryItem>{

    PageResult page(DictionaryItemParam param);

    RestResult<?> save(String id, String name, Integer num, String typeId, String value);

    RestResult<?> findDict();

}
