package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.springbootbean.mapper.sql.TenantConnectPrivilegeMapper;
import cn.com.tzy.springbootbean.mapper.sql.TenantMapper;
import cn.com.tzy.springbootbean.service.api.TenantConnectPrivilegeService;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.dome.sys.TenantConnectPrivilege;
import cn.com.tzy.springbootstarterredis.common.RedisCommon;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantConnectPrivilegeServiceImpl  extends ServiceImpl<TenantConnectPrivilegeMapper, TenantConnectPrivilege> implements TenantConnectPrivilegeService {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public RestResult<?> findPositionPrivilegeList(Long tenantId) {
        List<String> privilegeList = baseMapper.findTenantPrivilegeList(tenantId);
        return RestResult.result(RespCode.CODE_0.getValue(),null,privilegeList);
    }

    @Override
    @CacheEvict(value = RedisCommon.USER_INFO,allEntries = true)
    public RestResult<?> save(Long tenantId, List<String> privilegeList) {
        if(tenantId == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取租户编号");
        }
        Tenant tenant = tenantMapper.selectById(tenantId);
        if(tenant == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取租户信息");
        }
        List<String> privileges = baseMapper.findTenantPrivilegeList(tenant.getId());
        //要删除的值
        List<String> deleteList =privileges.stream().filter(num -> !privilegeList.contains(num)).collect(Collectors.toList());
        //要添加的值
        List<String> addList = privilegeList.stream().filter(num -> !privileges.contains(num)).collect(Collectors.toList());
        if(deleteList.size() > 0){
            baseMapper.deleteTenantConnectPrivilege(tenant.getId(),deleteList);
        }
        if(addList.size() > 0){
            baseMapper.saveTenantConnectPrivilege(tenant.getId(),addList);
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"保存成功");
    }
}
