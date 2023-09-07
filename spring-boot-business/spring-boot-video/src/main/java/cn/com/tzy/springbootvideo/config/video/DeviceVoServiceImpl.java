package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.Device;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceConvert;
import cn.com.tzy.springbootvideo.service.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Log4j2
@Component
public class DeviceVoServiceImpl extends DeviceVoService {

    @Resource
    private DeviceService deviceService;

    @Override
    public List<DeviceVo> getAllOnlineDevice() {
        List<Device> list = deviceService.list(new LambdaQueryWrapper<Device>().eq(Device::getOnline, ConstEnum.Flag.YES.getValue()));
        return DeviceConvert.INSTANCE.convertVo(list);
    }

    @Override
    public DeviceVo findDeviceGbId(String deviceGbId) {
        if(StringUtils.isEmpty(deviceGbId)){
            log.error("[查询设备信息失败] deviceGbId is null");
            return null;
        }
        Device one = deviceService.getOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceGbId));
        return DeviceConvert.INSTANCE.convert(one);
    }

    @Override
    public DeviceVo findPlatformIdChannelId(String platformId, String channelId) {
        Device device =  deviceService.findPlatformIdChannelId(platformId,channelId);
        return DeviceConvert.INSTANCE.convert(device);
    }

    @Override
    public DeviceVo findDeviceInfoPlatformIdChannelId(String platformId, String channelId) {
        Device device =  deviceService.findDeviceInfoPlatformIdChannelId(platformId,channelId);
        return DeviceConvert.INSTANCE.convert(device);
    }

    @Override
    public List<DeviceVo> queryDeviceWithAsMessageChannel() {
        List<Device> list = deviceService.list(new LambdaQueryWrapper<Device>().eq(Device::getOnline, ConstEnum.Flag.YES.getValue()).eq(Device::getAsMessageChannel,ConstEnum.Flag.YES.getValue()));
        return DeviceConvert.INSTANCE.convertVo(list);
    }

    @Override
    public int save(DeviceVo deviceVo) {
        boolean save ;
        Device convert = DeviceConvert.INSTANCE.convert(deviceVo);
        Device device = deviceService.getOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, convert.getDeviceId()));
        if(device == null){
            save = deviceService.save(convert);
        }else {
            convert.setId(device.getId());
            save = deviceService.updateById(convert);
        }
        return save?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();


    }

    @Override
    public void updateStatus(Long id, boolean status) {
        if(id == null){
            log.error("[设备状态更变] ：未获取设备编码");
            return;
        }
        deviceService.updateById(Device.builder().id(id).online(status?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue()).registerTime(status?new Date():null).build());
    }

    @Override
    public void updateZlm(String deviceGbId, String mediaServerId) {
        deviceService.update(new LambdaUpdateWrapper<Device>().set(Device::getMediaServerId,mediaServerId).eq(Device::getDeviceId,deviceGbId));
    }
}
