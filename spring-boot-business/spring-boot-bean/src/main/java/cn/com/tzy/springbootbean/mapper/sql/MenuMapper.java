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
    List<Map> findSelect(@Param("menuName") String menuName);

    /**
     * 根据编号查询
     *
     * @param id
     * @return
     */
    Map find(@Param("id") String id);

    /**
     * 查询菜单权限树
     *
     * @return
     */
    List<Map> findMenuPrivilegeTree();
}