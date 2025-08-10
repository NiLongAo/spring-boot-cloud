package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.PositionConnectMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionConnectMenuMapper extends BaseMapper<PositionConnectMenu> {

    List<String> findPositionPrivilegeList(@Param("positionId") Long positionId);

    int savePositionConnectMenu(@Param("positionId") Long positionId, @Param("menuIdList") List<String> menuIdList);

    int deletePositionConnectMenu(@Param("positionId") Long positionId, @Param("menuIdList") List<String> menuIdList);
}
