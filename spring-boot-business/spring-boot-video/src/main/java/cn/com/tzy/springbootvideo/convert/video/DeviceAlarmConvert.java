package cn.com.tzy.springbootvideo.convert.video;

import cn.com.tzy.springbootentity.dome.video.DeviceAlarm;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceAlarmVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceAlarmConvert {
    DeviceAlarmConvert INSTANCE = Mappers.getMapper(DeviceAlarmConvert.class);

    DeviceAlarmVo convert(DeviceAlarm param);

    DeviceAlarm convert(DeviceAlarmVo param);
}
