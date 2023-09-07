package cn.com.tzy.springbootbean.service.api.impl;

import cn.com.tzy.spingbootstartermybatis.core.mapper.utils.MyBatisUtils;
import cn.com.tzy.spingbootstartermybatis.core.query.LambdaQueryWrapperX;
import cn.com.tzy.spingbootstartermybatis.core.tenant.utils.TenantUtils;
import cn.com.tzy.springbootbean.convert.bean.UserConvert;
import cn.com.tzy.springbootbean.convert.sys.TenantConvert;
import cn.com.tzy.springbootbean.mapper.sql.TenantMapper;
import cn.com.tzy.springbootbean.service.api.TenantService;
import cn.com.tzy.springbootbean.service.api.UserService;
import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import cn.com.tzy.springbootentity.vo.bean.TenantUserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {
    @Autowired
    private UserService userService;

    @Override
    public PageResult page(TenantParam param) {
        Page<Tenant> page = MyBatisUtils.buildPage(param);
        LambdaQueryWrapperX<Tenant> tenantLambdaQueryWrapperX = new LambdaQueryWrapperX<Tenant>()
                .likeIfPresent(Tenant::getTenantName, param.getTenantName())
                .likeIfPresent(Tenant::getTenantUserName, param.getTenantUserName())
                .eqIfPresent(Tenant::getStatus, param.getStatus());
        return MyBatisUtils.selectPage(baseMapper, page, tenantLambdaQueryWrapperX);
    }

    @Override
    public RestResult<?> tenantSelect(List<Long> tenantIdList, String tenantName, Integer limit) {
        List<Tenant> tenantList = new ArrayList<>();
        if(!ObjectUtils.isEmpty(tenantIdList)){
            tenantList.addAll(baseMapper.selectBatchIds(tenantIdList));
        }
        LambdaQueryWrapperX<Tenant> tenantLambdaQueryWrapperX = new LambdaQueryWrapperX<Tenant>().likeIfPresent(Tenant::getTenantName, tenantName);
        if(tenantIdList != null && !tenantIdList.isEmpty()){
            tenantLambdaQueryWrapperX.and(obj->obj.notIn(Tenant::getId,tenantIdList));
        }
        tenantLambdaQueryWrapperX.last(String.format("limit %d",limit));
        tenantList.addAll(baseMapper.selectList(tenantLambdaQueryWrapperX));
        List<NotNullMap> data = new ArrayList<>();
        tenantList.forEach(obj ->{
            NotNullMap map = new NotNullMap();
            map.putLong("id",obj.getId());
            map.putString("name", obj.getTenantName());
            data.add(map);
        });
        return RestResult.result(RespCode.CODE_0.getValue(), null, data);
    }



    @Override
    public RestResult<?> insertTenant(TenantUserVo convert) {
        TenantParam tenantParam = convert.getTenant();
        UserParam userParam = convert.getUser();
        Tenant tenant = TenantConvert.INSTANCE.convert(tenantParam);
        User user = UserConvert.INSTANCE.convert(userParam);
        baseMapper.insert(tenant);
        TenantUtils.execute(tenant.getId(),()->{
            userService.insert(user, ConstEnum.Flag.NO.getValue(),ConstEnum.Flag.YES.getValue());
        });
        Tenant build = Tenant.builder().tenantUserId(user.getId()).tenantUserName(user.getUserName()).build();
        build.setId(tenant.getId());
        baseMapper.updateById(build);
        return RestResult.result(RespCode.CODE_0.getValue(),"新增成功");
    }

    @Override
    public RestResult<?> updateTenant(Tenant convert) {
        baseMapper.updateById(convert);
        return RestResult.result(RespCode.CODE_0.getValue(),"修改成功");
    }

    @Override
    public RestResult<?> removeTenant(Long id) {
        if(Constant.TENANT_ID.equals(id)){
            return  RestResult.result(RespCode.CODE_2.getValue(),"系统租户无法删除");
        }
        Tenant tenant = baseMapper.selectById(id);
        if(tenant == null){
            return  RestResult.result(RespCode.CODE_2.getValue(),"未获取到租户信息");
        }
        userService.remove(tenant.getTenantUserId());
        baseMapper.deleteById(tenant.getId());
        return RestResult.result(RespCode.CODE_0.getValue(),"删除成功成功");
    }
}
