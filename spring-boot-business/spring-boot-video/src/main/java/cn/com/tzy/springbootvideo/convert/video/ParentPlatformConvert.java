package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface ParentPlatformConvert {
    ParentPlatformConvert INSTANCE = Mappers.getMapper(ParentPlatformConvert.class);


    ParentPlatformVo convert(ParentPlatform param);

    List<ParentPlatformVo> convertVo(List<ParentPlatform> param);

    ParentPlatform convert(ParentPlatformVo param);

    List<ParentPlatform> convertBean(List<ParentPlatformVo> param);

}
