package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.RoleConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleConnectPrivilegeService extends IService<RoleConnectPrivilege>{

    RestResult<?> findRolePrivilegeList(Long roleId);

    RestResult<?> save(Long roleId, List<String> privilegeList);

}
