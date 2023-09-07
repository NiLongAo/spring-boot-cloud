package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectPrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentConnectPrivilegeMapper extends BaseMapper<DepartmentConnectPrivilege> {

    List<String> findDepartmentPrivilegeList(@Param("departmentId") Long departmentId);

    int saveDepartmentConnectPrivilege(@Param("departmentId") Long departmentId, @Param("privilegeList") List<String> privilegeList);
    int deleteIdList(@Param("departmentId") Long departmentId, @Param("privilegeList") List<String> privilegeList);
}
