package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询下拉树
     *
     * @param menuName
     * @return
     */
    List<Map> findAvailableTree(@Param("isShowPrivilege") Integer isShowPrivilege, @Param("menuName") String menuName);

    /**
     * 获取用户的角色菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findUserTenantMenu(@Param("userId") Long userId);

    /**
     * 获取用户的角色菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findUserRoleMenu(@Param("userId") Long userId);

    /**
     * 获取用户的部门菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findUserDepartmentMenu(@Param("userId") Long userId);

    /**
     * 获取用户的职位菜单
     *
     * @param userId
     * @return
     */
    List<Menu> findUserPositionMenu(@Param("userId") Long userId);

    /**
     * 业务需求字段
     */
    List<Menu> findSelect(@Param("menuName") String menuName);

    /**
     * 查询菜单权限树
     *
     * @return
     */
    List<Menu> findMenuPrivilegeTree();

    List<Menu> findTenantMenuPrivilegeTree(@Param("tenantId") Long tenantId);
    /**
     * 查询业务权限菜单
     * @param bizType 1.租户 2.部门  3.职位  4.角色
     * @param bizId 租户id或用户id
     * @return
     */
    List<Menu> findTypeButtonMenu(@Param("bizType") Integer bizType, @Param("bizId") Long bizId);
}