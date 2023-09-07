package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.StreamPush;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamPushVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StreamPushConvert {

    StreamPushConvert INSTANCE = Mappers.getMapper(StreamPushConvert.class);

    StreamPushVo convert(StreamPush param);

    List<StreamPushVo> convertVo(List<StreamPush> param);

    StreamPush convert(StreamPushVo param);

    List<StreamPush> convertBean(List<StreamPushVo> param);
}
