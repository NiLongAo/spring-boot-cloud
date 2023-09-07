package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.Logs;
import cn.com.tzy.springbootentity.param.bean.LogsParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogsMapper extends BaseMapper<Logs> {

    int findPageCount(LogsParam param);

    List<Logs> findPageResult(LogsParam param);
}