package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.TenantConnectMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TenantConnectMenuService extends IService<TenantConnectMenu> {


    RestResult<?> findPositionPrivilegeList(Long tenantId);

    RestResult<?> save(Long tenantId, List<String> privilegeList);


}
