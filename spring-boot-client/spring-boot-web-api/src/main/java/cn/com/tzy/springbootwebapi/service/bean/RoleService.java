package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import cn.com.tzy.springbootfeignbean.api.bean.RoleServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    public RoleServiceFeign roleServiceFeign;


    public PageResult page( RoleParam userPageModel){return roleServiceFeign.page(userPageModel);}

    public RestResult<?> findAll(){return roleServiceFeign.findAll();}

    public RestResult<?> save(RoleParam param){return roleServiceFeign.save(param);}

    public RestResult<?> remove(Long id){return roleServiceFeign.remove(id);}

    public RestResult<?> detail(Long id){return roleServiceFeign.detail(id);}

    public RestResult<?> select(List<Long> roleIdList, String roleName, Integer limit){
        return roleServiceFeign.select(roleIdList,roleName,limit);
    }
}
