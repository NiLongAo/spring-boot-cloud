package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeviceConvert {

    DeviceConvert INSTANCE = Mappers.getMapper(DeviceConvert.class);

    DeviceVo convert(Device param);

    List<DeviceVo> convertVo(List<Device> param);

    Device convert(DeviceVo param);
    List<Device> convertBean(List<DeviceVo> param);

}
