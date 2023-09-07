package cn.com.tzy.springbootbean.mapper.sql;

import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    int findPageCount(RoleParam param);

    List<Role> findPageResult(RoleParam param);

    List<Role> selectNameLimit(@Param("roleIdList") List<Long> roleIdList, @Param("roleName") String roleName, @Param("limit") Integer limit);
}
