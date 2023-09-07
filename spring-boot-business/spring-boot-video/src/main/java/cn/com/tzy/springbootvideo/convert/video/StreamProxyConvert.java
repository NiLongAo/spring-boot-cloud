package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamProxyVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StreamProxyConvert {

    StreamProxyConvert INSTANCE = Mappers.getMapper(StreamProxyConvert.class);


    StreamProxyVo convert(StreamProxy param);

    List<StreamProxyVo> convertVo(List<StreamProxy> param);

    StreamProxy convert(StreamProxyVo param);

    List<StreamProxy> convertBean(List<StreamProxyVo> param);
}
