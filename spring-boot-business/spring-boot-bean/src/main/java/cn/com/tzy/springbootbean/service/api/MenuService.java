package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MenuService extends IService<Menu> {
    RestResult<?> tree(String topName,Integer isShowPrivilege,String menuName);

    PageResult page(MenuParam param);

    RestResult<?> menuPrivilegeTree();

    RestResult<?> save(MenuParam param);

    RestResult<?> findUserTreeMenu(Long userId) throws Exception;

    RestResult<?> tenantMenuPrivilegeTree(Long tenantId) throws Exception;

    /**
     * 查询业务权限菜单
     * @param bizType 1.租户 2.部门  3.职位  4.角色
     * @param bizId 租户id或用户id
     * @return
     */
    List<Menu> findTypeButtonMenu(Integer bizType,Long bizId);

    /**
     * 权限初始化
     * @return
     */
    RestResult<?> initBottom();

}


