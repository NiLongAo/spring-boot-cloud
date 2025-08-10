package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentConnectMenuMapper extends BaseMapper<DepartmentConnectMenu> {

    List<String> findDepartmentPrivilegeList(@Param("departmentId") Long departmentId);

    int saveDepartmentConnectMenu(@Param("departmentId") Long departmentId, @Param("menuIdList") List<String> menuIdList);
    int deleteIdList(@Param("departmentId") Long departmentId, @Param("menuIdList") List<String> menuIdList);
}
