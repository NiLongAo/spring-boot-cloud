package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootstartervideobasic.vo.video.GbStreamVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GbStreamConvert {

    GbStreamConvert INSTANCE = Mappers.getMapper(GbStreamConvert.class);

    GbStreamVo convert(GbStream param);

    List<GbStreamVo> convertListVo(List<GbStream> param);

    GbStream convert(GbStreamVo param);
}
