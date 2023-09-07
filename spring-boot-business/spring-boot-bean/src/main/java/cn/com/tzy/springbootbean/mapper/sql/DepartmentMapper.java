package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Department;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    List<Map> findAvailableTree(@Param("departmentName") String departmentName);

    List<Department> selectDepartmentList(DepartmentParam param);

    List<Department> selectNameLimit(@Param("departmentIdList") List<Long> departmentIdList, @Param("departmentName") String departmentName, @Param("limit") Integer limit);
}
