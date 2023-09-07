package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.DeviceMobilePosition;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceMobilePositionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceMobilePositionConvert {

    DeviceMobilePositionConvert INSTANCE = Mappers.getMapper(DeviceMobilePositionConvert.class);

    DeviceMobilePositionVo convert(DeviceMobilePosition param);

    DeviceMobilePosition convert(DeviceMobilePositionVo param);
}
