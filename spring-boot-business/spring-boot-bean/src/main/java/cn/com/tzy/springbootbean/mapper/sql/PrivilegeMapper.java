package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Privilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface PrivilegeMapper extends BaseMapper<Privilege> {

    /**
     * 系统管理员用户的角色权限
     *
     * @return
     */
    Set<String> findUserAdmin();

    /**
     * 获取租户所有权限
     *
     * @return
     */
    Set<String> findTenantPrivilegeList(@Param("tenantId") Long tenantId);
    /**
     * 获取用户的角色权限
     *
     * @param userId
     * @return
     */
    Set<String> findUserRolePrivilegeList(@Param("userId") Long userId);

    /**
     * 获取用户的部门权限
     *
     * @param userId
     * @return
     */
    Set<String> findUserDepartmentPrivilegeList(@Param("userId") Long userId);

    /**
     * 获取用户的职位权限
     *
     * @param userId
     * @return
     */
    Set<String> findUserPositionPrivilegeList(@Param("userId") Long userId);

    /**
     * 获取所有可使用的权限信息
     *
     * @return
     */
    List<Privilege> findEnabledAll(@Param("isOpen") Integer isOpen);

    List<Map> findMenuList(@Param("idList") List<String> idList);

    List<Map> findMenuPrivilegeTree(@Param("idList") List<String> idList);

    List<Map> findTenantMenuPrivilegeTree(@Param("tenantId") Long tenantId, @Param("idList") List<String> idList);
}