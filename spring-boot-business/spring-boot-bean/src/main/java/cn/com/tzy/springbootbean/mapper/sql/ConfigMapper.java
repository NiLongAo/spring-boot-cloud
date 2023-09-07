package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.Config;
import cn.com.tzy.springbootentity.param.sys.ConfigParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    int findPageCount(ConfigParam param);

    List<Config> findPageResult(ConfigParam param);
}