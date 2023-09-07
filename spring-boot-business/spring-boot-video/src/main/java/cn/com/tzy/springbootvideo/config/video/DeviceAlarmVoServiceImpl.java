package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootentity.dome.video.DeviceAlarm;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceAlarmVo;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceAlarmVoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceAlarmConvert;
import cn.com.tzy.springbootvideo.mapper.DeviceAlarmMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DeviceAlarmVoServiceImpl extends DeviceAlarmVoService {

    @Resource
    private DeviceAlarmMapper deviceAlarmMapper;

    @Override
    public void insert(DeviceAlarmVo deviceAlarmVo) {
        DeviceAlarm convert = DeviceAlarmConvert.INSTANCE.convert(deviceAlarmVo);
        deviceAlarmMapper.insert(convert);
    }
}
