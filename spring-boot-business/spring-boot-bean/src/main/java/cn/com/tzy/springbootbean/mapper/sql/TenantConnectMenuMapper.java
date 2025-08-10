package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.sys.TenantConnectMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TenantConnectMenuMapper extends BaseMapper<TenantConnectMenu> {

    List<String> findTenantPrivilegeList(@Param("tenantId") Long tenantId);

    int saveTenantConnectMenu(@Param("tenantId") Long tenantId, @Param("menuIdList") List<String> menuIdList);

    int deleteTenantConnectMenu(@Param("tenantId") Long tenantId, @Param("menuIdList") List<String> menuIdList);
}