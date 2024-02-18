package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.ParentPlatform;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideobasic.vo.sip.ChannelSourceInfo;
import cn.com.tzy.springbootstartervideocore.service.video.ParentPlatformVoService;
import cn.com.tzy.springbootvideo.convert.video.ParentPlatformConvert;
import cn.com.tzy.springbootvideo.service.PlatformGbChannelService;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ParentPlatformVoServiceImpl extends ParentPlatformVoService {

    @Resource
    private PlatformGbStreamService platformGbStreamService;
    @Resource
    private PlatformGbChannelService platformGbChannelService;
    @Resource
    private cn.com.tzy.springbootvideo.service.ParentPlatformService parentPlatformService;

    @Override
    public ParentPlatformVo getParentPlatformByServerGbId(String platformGbId) {
        if(StringUtils.isEmpty(platformGbId)){
            log.info("[上级平台 ] ： 未获取上级平台编号");
            return  null;
        }
        ParentPlatform one = parentPlatformService.getOne(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getServerGbId, platformGbId));
        if(ObjectUtil.isEmpty(one)){
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convert(one);
    }

    @Override
    public List<ParentPlatformVo> getParentPlatformByDeviceGbId(String deviceGbId) {
        if(StringUtils.isEmpty(deviceGbId)){
            log.info("[上级平台 ] ： 未获取注册平台编号");
            return  null;
        }
        List<ParentPlatform> list = parentPlatformService.list(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getDeviceGbId, deviceGbId));
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public List<ParentPlatformVo> getParentPlatformByServerGbIdList(List<String> platformGbId) {
        if(platformGbId == null ||platformGbId.isEmpty()){
            log.info("[上级平台 ] ： 未获取上级平台编号");
            return  null;
        }
        List<ParentPlatform> list = parentPlatformService.list(new LambdaQueryWrapper<ParentPlatform>().in(ParentPlatform::getServerGbId, platformGbId));
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public int updateParentPlatformStatus(String platformGbId, boolean online) {
        if(StringUtils.isEmpty(platformGbId)){
            log.info("[上级平台 ] ： 未获取上级平台编号");
            return ConstEnum.Flag.NO.getValue();
        }
        boolean update = parentPlatformService.update(new LambdaUpdateWrapper<ParentPlatform>().set(ParentPlatform::getStatus, online ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue()));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public List<ParentPlatformVo> queryEnableParentPlatformList() {
        List<ParentPlatform> list = parentPlatformService.list(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getEnable, ConstEnum.Flag.YES.getValue()));
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public List<ParentPlatformVo> queryEnablePlatformListWithAsMessageChannel() {
        List<ParentPlatform> list = parentPlatformService.list(new LambdaQueryWrapper<ParentPlatform>().eq(ParentPlatform::getEnable, ConstEnum.Flag.YES.getValue()).eq(ParentPlatform::getAsMessageChannel,ConstEnum.Flag.YES.getValue()));
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public List<ChannelSourceInfo> getChannelSource(String serverGbId, String channelId) {
        List<Map> mapList = parentPlatformService.findChannelSource(serverGbId,channelId);
        return mapList.stream().map(o-> ChannelSourceInfo.builder().name(MapUtil.getStr(o,"name")).count(MapUtil.getInt(o,"count")).build()).collect(Collectors.toList());
    }

    @Override
    public List<ParentPlatformVo> queryPlatFormListForGBWithGBId(String channelId, List<String> allPlatformId) {
        if(StringUtils.isEmpty(channelId) || allPlatformId == null || allPlatformId.isEmpty()){
            log.info("[上级平台 ] ：  ： 信息不存在 channelId :{},  allPlatformId :{}",channelId,allPlatformId);
            return  null;
        }
        List<ParentPlatform> list = parentPlatformService.queryPlatFormListForGBWithGBId(channelId,allPlatformId);
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public List<ParentPlatformVo> queryPlatFormListForStreamWithGBId(String app, String stream, List<String> allPlatformId) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[上级平台 ] ： 信息不存在 app :{}, stream :{}",app,stream);
            return  null;
        }
        List<ParentPlatform> list = parentPlatformService.queryPlatFormListForStreamWithGBId(app,stream,allPlatformId);
        if(list.isEmpty()){
            log.info("[上级平台 ] ： 未获取到上级平台信息");
            return  null;
        }
        return ParentPlatformConvert.INSTANCE.convertVo(list);
    }

    @Override
    public void delPlatformGbStream(String app, String stream) {
        platformGbStreamService.delPlatformGbStream(app,stream);
    }

    @Override
    public List<ParentPlatformVo> findPlatformGbChannel(String channelId) {
        List<ParentPlatform> list = platformGbChannelService.findChannelIdList(channelId);
       return ParentPlatformConvert.INSTANCE.convertVo(list);
    }
}
