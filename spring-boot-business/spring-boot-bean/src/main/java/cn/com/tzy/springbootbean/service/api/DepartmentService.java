package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Department;
import cn.com.tzy.springbootentity.param.bean.DepartmentParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DepartmentService extends IService<Department>{

    PageResult page(DepartmentParam param);

    RestResult<?> save(Long parentId, Long id, String departmentName, Integer isEnable, String memo);

    RestResult<?> tree(String topName,String departmentName);

    RestResult<?> positionSelect(List<Long> departmentIdList, String departmentName, Integer limit);
}
