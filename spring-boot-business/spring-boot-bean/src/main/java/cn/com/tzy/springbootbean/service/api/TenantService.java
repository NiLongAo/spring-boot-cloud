package cn.com.tzy.springbootbean.service.api;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TenantService extends IService<Tenant> {

    PageResult page(TenantParam param);


    RestResult<?> tenantSelect(List<Long> tenantIdList, String tenantName, Integer limit);

    RestResult<?> removeTenant(Long id);

    RestResult<?> insertTenant(TenantUserVo convert);

    RestResult<?> updateTenant(Tenant convert);
}
