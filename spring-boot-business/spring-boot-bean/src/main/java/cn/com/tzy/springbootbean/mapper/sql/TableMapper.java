package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootcomm.common.model.DictModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TableMapper {
    /**
     * 获取字典数据
     * @param table 表名
     * @param text 名称
     * @param code code
     * @return
     */
    List<DictModel> selectDiceData(@Param("table") String table, @Param("text") String text, @Param("code") Integer code);
}
