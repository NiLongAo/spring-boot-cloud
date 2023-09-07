package cn.com.tzy.springbootbean.convert.sys;

import cn.com.tzy.springbootentity.dome.sys.Tenant;
import cn.com.tzy.springbootentity.param.sys.TenantParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    Tenant convert(TenantParam param);


}
