package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.param.bean.RoleParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role>{

    PageResult page(RoleParam param);

    RestResult<?> save(Long id, String memo, String roleName);

    RestResult<?> roleSelect(List<Long> roleIdList, String roleName, Integer limit);
}
