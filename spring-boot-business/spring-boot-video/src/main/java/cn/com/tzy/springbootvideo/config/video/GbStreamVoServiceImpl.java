package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.DeviceChannel;
import cn.com.tzy.springbootentity.dome.video.GbStream;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.GbStreamVo;
import cn.com.tzy.springbootstartervideocore.properties.VideoProperties;
import cn.com.tzy.springbootstartervideocore.service.video.GbStreamVoService;
import cn.com.tzy.springbootvideo.convert.video.DeviceChannelConvert;
import cn.com.tzy.springbootvideo.convert.video.GbStreamConvert;
import cn.com.tzy.springbootvideo.service.DeviceChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@Component
public class GbStreamVoServiceImpl extends GbStreamVoService {

    @Resource
    private cn.com.tzy.springbootvideo.service.GbStreamService gbStreamService;
    @Resource
    private VideoProperties videoProperties;
    @Resource
    private DeviceChannelService deviceChannelService;

    @Override
    public GbStreamVo findPlatformId(String platformId, String gbId) {
        GbStream gbStream = gbStreamService.findPlatformId(platformId,gbId);
        return GbStreamConvert.INSTANCE.convert(gbStream);
    }

    @Override
    public GbStreamVo findAppStream(String app, String steamId) {
        GbStream gbStream = gbStreamService.getOne(new LambdaUpdateWrapper<GbStream>().eq(GbStream::getApp,app).eq(GbStream::getStream,steamId));
        if(gbStream == null){
            log.info("[直播流相关] 未获取到直播流相关信息");
            return null;
        }
        return GbStreamConvert.INSTANCE.convert(gbStream);
    }

    @Override
    public int delAppStream(String app, String steamId) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(steamId)){
            return ConstEnum.Flag.NO.getValue();
        }
        boolean remove = gbStreamService.remove(new LambdaQueryWrapper<GbStream>().eq(GbStream::getApp, app).eq(GbStream::getStream, steamId));
        if(remove){
            return ConstEnum.Flag.YES.getValue();
        }else {
            return ConstEnum.Flag.NO.getValue();
        }

    }

    @Override
    public int update(GbStreamVo param) {
        if(StringUtils.isEmpty(param.getApp()) || StringUtils.isEmpty(param.getStream())){
            return ConstEnum.Flag.NO.getValue();
        }
        GbStream convert = GbStreamConvert.INSTANCE.convert(param);
        boolean update = gbStreamService.update(convert, new LambdaQueryWrapper<GbStream>().eq(GbStream::getApp, param.getApp()).eq(GbStream::getStream, param.getStream()));
        return update?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue();
    }

    @Override
    public List<DeviceChannelVo> queryGbStreamListInPlatform(String serverGbId,String gbId) {
        List<DeviceChannel> deviceChannelList = deviceChannelService.queryGbStreamListInPlatform(serverGbId,gbId,videoProperties.getUsePushingAsStatus());
        return DeviceChannelConvert.INSTANCE.convertVoList(deviceChannelList);
    }
}
