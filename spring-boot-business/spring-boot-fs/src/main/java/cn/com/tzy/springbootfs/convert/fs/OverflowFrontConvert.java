package cn.com.tzy.springbootfs.convert.fs;

import cn.com.tzy.springbootentity.dome.fs.OverflowFront;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.OverflowFrontInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OverflowFrontConvert {
    OverflowFrontConvert INSTANCE = Mappers.getMapper(OverflowFrontConvert.class);
    OverflowFront convert(OverflowFrontInfo param);

    List<OverflowFront> convertOverflowExpList(List<OverflowFrontInfo> param);
    OverflowFrontInfo convert(OverflowFront param);
    List<OverflowFrontInfo> convertOverflowExpInfoList(List<OverflowFront> param);
}
