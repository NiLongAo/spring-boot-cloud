package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.RoleConnectPrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleConnectPrivilegeMapper extends BaseMapper<RoleConnectPrivilege> {

    List<String> findRolePrivilegeList(@Param("roleId") Long roleId);


    int saveRoleConnectPrivilege(@Param("roleId") Long roleId, @Param("privilegeList") List<String> privilegeList);
    int deleteRoleConnectPrivilege(@Param("roleId") Long roleId, @Param("privilegeList") List<String> privilegeList);
}
