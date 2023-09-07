package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.DepartmentConnectPrivilegeMapper;
import cn.com.tzy.springbootbean.mapper.sql.DepartmentMapper;
import cn.com.tzy.springbootbean.service.api.DepartmentConnectPrivilegeService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Department;
import cn.com.tzy.springbootentity.dome.bean.DepartmentConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentConnectPrivilegeServiceImpl extends ServiceImpl<DepartmentConnectPrivilegeMapper, DepartmentConnectPrivilege> implements DepartmentConnectPrivilegeService{

    @Autowired
    DepartmentMapper departmentMapper;

    @Override
    public RestResult<?> findDepartmentPrivilegeList(Long departmentId) {
        List<String> privilegeList = baseMapper.findDepartmentPrivilegeList(departmentId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,privilegeList);
    }

    @Override
    public RestResult<?> save(Long departmentId, List<String> privilegeList) {
        if(departmentId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取部门编号");
        }
        Department department = departmentMapper.selectById(departmentId);
        if(department == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取部门信息");
        }
        List<String> privileges = baseMapper.findDepartmentPrivilegeList(departmentId);
        //要删除的值
        List<String> deleteList =privileges.stream().filter(num -> !privilegeList.contains(num)).collect(Collectors.toList());
        //要添加的值
        List<String> addList = privilegeList.stream().filter(num -> !privileges.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteIdList(department.getId(),deleteList);
        }
        if(addList.size() > 0){
            baseMapper.saveDepartmentConnectPrivilege(department.getId(),addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
