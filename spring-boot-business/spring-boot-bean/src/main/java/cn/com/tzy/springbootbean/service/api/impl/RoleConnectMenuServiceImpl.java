package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.RoleConnectMenuMapper;
import cn.com.tzy.springbootbean.mapper.sql.RoleMapper;
import cn.com.tzy.springbootbean.service.api.RoleConnectMenuService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.bean.Role;
import cn.com.tzy.springbootentity.dome.bean.RoleConnectMenu;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleConnectMenuServiceImpl extends ServiceImpl<RoleConnectMenuMapper, RoleConnectMenu> implements RoleConnectMenuService{

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public RestResult<?> findRolePrivilegeList(Long roleId) {
        List<String> privilegeList = baseMapper.findRolePrivilegeList(roleId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,privilegeList);
    }

    @Override
    public RestResult<?> save(Long roleId, List<String> menuIdList) {
        if(roleId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取角色编号");
        }
        Role role = roleMapper.selectById(roleId);
        if(role == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取角色信息");
        }
        List<String> privileges = baseMapper.findRolePrivilegeList(roleId);
        //要删除的值
        List<String> deleteList =privileges.stream().filter(num -> !menuIdList.contains(num)).collect(Collectors.toList());
        //要添加的值
        List<String> addList = menuIdList.stream().filter(num -> !privileges.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteRoleConnectMenu(role.getId(),deleteList);
        }
        if(addList.size() > 0){
            baseMapper.saveRoleConnectMenu(role.getId(),addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
