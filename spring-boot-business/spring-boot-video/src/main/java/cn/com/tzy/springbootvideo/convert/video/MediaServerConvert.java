package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.MediaServer;
import cn.com.tzy.springbootentity.param.video.MediaServerSaveParam;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MediaServerConvert {

    MediaServerConvert INSTANCE = Mappers.getMapper(MediaServerConvert.class);

    MediaServerVo convert(MediaServer param);

    List<MediaServerVo> convertVo(List<MediaServer> param);

    MediaServer convert(MediaServerVo param);

    List<MediaServer> convertBean(List<MediaServerVo> param);

    MediaServer convert(MediaServerSaveParam param);
}
