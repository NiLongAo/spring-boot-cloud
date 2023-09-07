package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.PlatformCatalog;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.PlatformCatalogVo;
import cn.com.tzy.springbootstartervideocore.service.video.PlatformCatalogVoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceChannelConvert;
import cn.com.tzy.springbootvideo.convert.video.PlatformCatalogConvert;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PlatformCatalogVoServiceImpl extends PlatformCatalogVoService {

    @Resource
    private cn.com.tzy.springbootvideo.service.PlatformCatalogService platformCatalogService;

    @Override
    public PlatformCatalogVo findId(String id) {
        PlatformCatalog entity = platformCatalogService.findId(id);
        return PlatformCatalogConvert.INSTANCE.convert(entity);
    }
    @Override
    public PlatformCatalogVo findChannelId(String channelId) {
        PlatformCatalog entity = platformCatalogService.findId(channelId);
        return PlatformCatalogConvert.INSTANCE.convert(entity);
    }

    @Override
    public List<DeviceChannelVo> queryCatalogInPlatform(String serverGbId) {
        List<DeviceChannel> deviceChannels =platformCatalogService.queryCatalogInPlatform(serverGbId);
        return DeviceChannelConvert.INSTANCE.convertVoList(deviceChannels);
    }
}
