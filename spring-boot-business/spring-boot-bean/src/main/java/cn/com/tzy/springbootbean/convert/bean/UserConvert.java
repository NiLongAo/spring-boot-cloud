package cn.com.tzy.springbootbean.convert.bean;

import cn.com.tzy.springbootentity.dome.bean.User;
import cn.com.tzy.springbootentity.param.bean.UserParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    User convert(UserParam param);

}
