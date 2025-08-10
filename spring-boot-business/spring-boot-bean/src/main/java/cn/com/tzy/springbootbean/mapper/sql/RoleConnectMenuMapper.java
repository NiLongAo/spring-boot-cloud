package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.RoleConnectMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleConnectMenuMapper extends BaseMapper<RoleConnectMenu> {

    List<String> findRolePrivilegeList(@Param("roleId") Long roleId);


    int saveRoleConnectMenu(@Param("roleId") Long roleId, @Param("menuIdList") List<String> menuIdList);
    int deleteRoleConnectMenu(@Param("roleId") Long roleId, @Param("menuIdList") List<String> menuIdList);
}
