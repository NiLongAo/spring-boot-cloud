package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.TenantConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TenantConnectPrivilegeService extends IService<TenantConnectPrivilege> {


    RestResult<?> findPositionPrivilegeList(Long tenantId);

    RestResult<?> save(Long tenantId, List<String> privilegeList);


}
