package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.DictionaryItem;
import cn.com.tzy.springbootentity.param.sys.DictionaryItemParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictionaryItemMapper extends BaseMapper<DictionaryItem> {

    int findPageCount(DictionaryItemParam param);

    List<DictionaryItem> findPageResult(DictionaryItemParam param);

    List<DictionaryItem> findDict();
}