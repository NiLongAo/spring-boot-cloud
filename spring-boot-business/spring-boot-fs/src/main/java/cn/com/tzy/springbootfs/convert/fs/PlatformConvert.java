package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlatformConvert {
    PlatformConvert INSTANCE = Mappers.getMapper(PlatformConvert.class);
    Platform convert(ConfigModel param);
    ConfigModel convert(Platform param);
}
