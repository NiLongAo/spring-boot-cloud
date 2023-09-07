package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DepartmentConnectPrivilegeService extends IService<DepartmentConnectPrivilege>{

    RestResult<?> findDepartmentPrivilegeList( Long departmentId);

    RestResult<?> save(Long departmentId, List<String> privilegeList);
}
