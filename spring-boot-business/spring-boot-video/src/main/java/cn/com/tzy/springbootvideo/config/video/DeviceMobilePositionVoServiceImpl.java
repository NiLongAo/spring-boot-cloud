package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootentity.dome.video.DeviceMobilePosition;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceMobilePositionVo;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceMobilePositionVoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceMobilePositionConvert;
import cn.com.tzy.springbootvideo.service.DeviceMobilePositionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DeviceMobilePositionVoServiceImpl extends DeviceMobilePositionVoService {

    @Resource
    private DeviceMobilePositionService deviceMobilePositionService;

    @Override
    public void save(DeviceMobilePositionVo deviceMobilePositionVo) {
        DeviceMobilePosition convert = DeviceMobilePositionConvert.INSTANCE.convert(deviceMobilePositionVo);
        DeviceMobilePosition deviceMobilePosition = deviceMobilePositionService.getOne(new LambdaQueryWrapper<DeviceMobilePosition>().eq(DeviceMobilePosition::getDeviceId, deviceMobilePositionVo.getDeviceId()).eq(DeviceMobilePosition::getChannelId, deviceMobilePositionVo.getChannelId()));
        if(deviceMobilePosition != null){
            convert.setId(deviceMobilePosition.getId());
        }
        deviceMobilePositionService.saveOrUpdate(convert);
    }

    @Override
    public DeviceMobilePositionVo findChannelId(String channelId) {
        DeviceMobilePosition deviceMobilePosition = deviceMobilePositionService.getOne(new LambdaQueryWrapper<DeviceMobilePosition>().eq(DeviceMobilePosition::getChannelId, channelId));
        if(deviceMobilePosition != null){
            return null;
        }
        return DeviceMobilePositionConvert.INSTANCE.convert(deviceMobilePosition);
    }
}
