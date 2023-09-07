package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.utils.JwtUtils;
import cn.com.tzy.springbootentity.common.info.UserPayload;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.MenuParam;
import cn.com.tzy.springbootcomm.utils.AppUtils;
import cn.com.tzy.springbootfeignbean.api.bean.MenuServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MenuService {

    @Autowired
    MenuServiceFeign menuServiceFeign;

    public RestResult<?> findUserTreeMenu(){
        Map map = JwtUtils.getJwtPayload();
        UserPayload user = AppUtils.convertValue2(map, UserPayload.class);
        return menuServiceFeign.findUserTreeMenu(user.getUserId());
    }

    public PageResult page(MenuParam userPageModel){
        return menuServiceFeign.page(userPageModel);
    }


    public RestResult<?> findAll(){
        return menuServiceFeign.findAll();
    }

    public RestResult<?> menuPrivilegeTree(){
        return menuServiceFeign.menuPrivilegeTree();
    }

    public RestResult<?> tenantMenuPrivilegeTree(Long tenantId){
        return menuServiceFeign.tenantMenuPrivilegeTree(tenantId);
    }

    public RestResult<?> tree(MenuParam param){
        return menuServiceFeign.tree(param);
    }


    public RestResult<?> save(MenuParam param){
        return menuServiceFeign.save(param);
    }

    public RestResult<?> remove(Long id){
        return menuServiceFeign.remove(id);
    }

    public RestResult<?> detail(String id){return menuServiceFeign.detail(id);}
}
