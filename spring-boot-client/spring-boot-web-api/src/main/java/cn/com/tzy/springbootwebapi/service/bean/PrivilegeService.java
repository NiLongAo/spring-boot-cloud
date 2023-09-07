package cn.com.tzy.springbootwebapi.service.bean;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.bean.DepartmentConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PositionConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.PrivilegeParam;
import cn.com.tzy.springbootentity.param.bean.RoleConnectPrivilegeParam;
import cn.com.tzy.springbootentity.param.sms.TenantConnectPrivilegeParam;
import cn.com.tzy.springbootfeignbean.api.bean.PrivilegeServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivilegeService {

    @Autowired
    PrivilegeServiceFeign privilegeServiceFeign;


    public RestResult<?> init (){
        return privilegeServiceFeign.init();
    }

    public RestResult<?> findAll(){return privilegeServiceFeign.findAll();}

    public RestResult<?> save(PrivilegeParam param){return privilegeServiceFeign.save(param);}

    public RestResult<?> remove(String id){return privilegeServiceFeign.remove(id);}

    public RestResult<?> detail(String id){return privilegeServiceFeign.detail(id);}

    public RestResult<?> findDepartmentPrivilegeList(Long departmentId){return privilegeServiceFeign.findDepartmentPrivilegeList(departmentId);}

    public RestResult<?> departmentPrivilegeSave(DepartmentConnectPrivilegeParam save){
        return privilegeServiceFeign.departmentPrivilegeSave(save);
    }

    public RestResult<?> findPositionPrivilegeList(Long positionId){return privilegeServiceFeign.findPositionPrivilegeList(positionId);}

    public RestResult<?> positionPrivilegeSave(PositionConnectPrivilegeParam save){
        return privilegeServiceFeign.positionPrivilegeSave(save);
    }

    public RestResult<?> findRolePrivilegeList(Long roleId){return privilegeServiceFeign.findRolePrivilegeList(roleId);}

    public RestResult<?> rolePrivilegeSave(RoleConnectPrivilegeParam save){
        return privilegeServiceFeign.rolePrivilegeSave(save);
    }

    public RestResult<?> findTenantPrivilegeList(Long tenantId){return privilegeServiceFeign.findTenantPrivilegeList(tenantId);}

    public RestResult<?> tenantPrivilegeSave(TenantConnectPrivilegeParam save){
        return privilegeServiceFeign.tenantPrivilegeSave(save);
    }

}
