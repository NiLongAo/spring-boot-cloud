package cn.com.tzy.springbootwebapi.service.config;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import cn.com.tzy.springbootfeignbean.api.sys.TenantServiceFeign;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private TenantServiceFeign feign;

    public RestResult<?> tenantSelect(List<Long> tenantIdList, String tenantName, Integer limit) {
        return feign.tenantSelect(tenantIdList,tenantName,limit);
    }

    public PageResult page(TenantParam param) {
        return feign.page(param);
    }

    @GlobalTransactional
    public RestResult<?> insert(TenantUserVo param) {
        return feign.insert(param);
    }

    @GlobalTransactional
    public RestResult<?> update(TenantParam param) {
        return feign.update(param);
    }

    @GlobalTransactional
    public RestResult<?> remove(Long id) {
        return feign.remove(id);
    }

    public RestResult<?> detail(Long id) {
        return feign.detail(id);
    }
}
