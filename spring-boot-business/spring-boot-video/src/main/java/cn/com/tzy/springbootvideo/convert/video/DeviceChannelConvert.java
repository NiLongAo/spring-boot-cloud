package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeviceChannelConvert {

    DeviceChannelConvert INSTANCE = Mappers.getMapper(DeviceChannelConvert.class);

    DeviceChannelVo convert(DeviceChannel param);

    DeviceChannel convert(DeviceChannelVo param);
    List<DeviceChannelVo> convertVoList(List<DeviceChannel> param);

    List<DeviceChannel> convertList(List<DeviceChannelVo> param);
}
