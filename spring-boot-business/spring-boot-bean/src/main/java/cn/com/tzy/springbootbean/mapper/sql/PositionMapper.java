package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Position;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PositionMapper extends BaseMapper<Position> {


    List<Map> findAvailableTree(@Param("positionName") String positionName);

    List<Position> selectPositionList(@Param("positionName") String positionName);

    List<Position> selectNameLimit(@Param("positionIdList") List<Long> positionIdList, @Param("positionName") String positionName, @Param("limit") Integer limit);
}
