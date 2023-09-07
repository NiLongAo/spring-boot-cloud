package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Menu;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MenuService extends IService<Menu> {
    RestResult<?> tree(String topName,Integer isShowPrivilege,String menuName);

    PageResult page(MenuParam param);

    RestResult<?> menuPrivilegeTree();

    RestResult<?> save(MenuParam param);

    RestResult<?> findUserTreeMenu(Long userId) throws Exception;

    RestResult<?> tenantMenuPrivilegeTree(Long tenantId) throws Exception;
}


