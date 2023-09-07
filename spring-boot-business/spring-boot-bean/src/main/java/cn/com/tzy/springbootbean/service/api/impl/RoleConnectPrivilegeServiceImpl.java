package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.RoleConnectPrivilegeMapper;
import cn.com.tzy.springbootbean.mapper.sql.RoleMapper;
import cn.com.tzy.springbootbean.service.api.RoleConnectPrivilegeService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.dome.bean.RoleConnectPrivilege;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleConnectPrivilegeServiceImpl extends ServiceImpl<RoleConnectPrivilegeMapper, RoleConnectPrivilege> implements RoleConnectPrivilegeService{

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public RestResult<?> findRolePrivilegeList(Long roleId) {
        List<String> privilegeList = baseMapper.findRolePrivilegeList(roleId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,privilegeList);
    }

    @Override
    public RestResult<?> save(Long roleId, List<String> privilegeList) {
        if(roleId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取角色编号");
        }
        Role role = roleMapper.selectById(roleId);
        if(role == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取角色信息");
        }
        List<String> privileges = baseMapper.findRolePrivilegeList(roleId);
        //要删除的值
        List<String> deleteList =privileges.stream().filter(num -> !privilegeList.contains(num)).collect(Collectors.toList());
        //要添加的值
        List<String> addList = privilegeList.stream().filter(num -> !privileges.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteRoleConnectPrivilege(role.getId(),deleteList);
        }
        if(addList.size() > 0){
            baseMapper.saveRoleConnectPrivilege(role.getId(),addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
