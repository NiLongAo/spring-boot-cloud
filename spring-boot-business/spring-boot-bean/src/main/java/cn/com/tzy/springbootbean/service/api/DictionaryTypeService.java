package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.DictionaryType;
import com.baomidou.mybatisplus.extension.service.IService;
public interface DictionaryTypeService extends IService<DictionaryType>{


    RestResult<?> save(String id, String code, String name, Integer status);
}
