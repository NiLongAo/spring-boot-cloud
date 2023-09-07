package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.PositionConnectPrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionConnectPrivilegeMapper extends BaseMapper<PositionConnectPrivilege> {

    List<String> findPositionPrivilegeList(@Param("positionId") Long positionId);

    int savePositionConnectPrivilege(@Param("positionId") Long positionId, @Param("privilegeList") List<String> privilegeList);

    int deletePositionConnectPrivilege(@Param("positionId") Long positionId, @Param("privilegeList") List<String> privilegeList);
}
