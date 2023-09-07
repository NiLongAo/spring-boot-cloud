package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Mini;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MiniMapper extends BaseMapper<Mini> {
    Mini findWebUserId(@Param("userId") Long userId);
}