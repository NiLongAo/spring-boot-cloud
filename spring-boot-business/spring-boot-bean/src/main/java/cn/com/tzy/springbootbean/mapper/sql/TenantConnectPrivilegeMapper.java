package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.TenantConnectPrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TenantConnectPrivilegeMapper extends BaseMapper<TenantConnectPrivilege> {

    List<String> findTenantPrivilegeList(@Param("tenantId") Long tenantId);

    int saveTenantConnectPrivilege(@Param("tenantId") Long tenantId, @Param("privilegeList") List<String> privilegeList);

    int deleteTenantConnectPrivilege(@Param("tenantId") Long tenantId, @Param("privilegeList") List<String> privilegeList);
}