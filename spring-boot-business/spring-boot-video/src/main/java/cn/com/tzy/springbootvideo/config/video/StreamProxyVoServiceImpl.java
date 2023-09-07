package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.StreamProxy;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamProxyVo;
import cn.com.tzy.springbootstartervideocore.service.video.StreamProxyVoService;
import cn.com.tzy.springbootvideo.convert.video.StreamProxyConvert;
import cn.com.tzy.springbootvideo.service.PlatformGbStreamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class StreamProxyVoServiceImpl extends StreamProxyVoService {

    @Resource
    private cn.com.tzy.springbootvideo.service.StreamProxyService streamProxyService;
    @Resource
    private PlatformGbStreamService platformGbStreamService;

    @Override
    public StreamProxyVo findAppStream(String app, String stream) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[拉流信息] 未获取到 app:{},stream:{} ",app,stream);
            return null;
        }
        StreamProxy one = streamProxyService.getOne(new LambdaQueryWrapper<StreamProxy>().eq(StreamProxy::getApp, app).eq(StreamProxy::getStream, stream));
        return StreamProxyConvert.INSTANCE.convert(one);
    }

    @Override
    public void updateEnable(Long id, ConstEnum.Flag flag) {
        if(id ==null || flag == null){
            log.info("[拉流信息] 未获取到 id:{},stream:{} ",id,flag);
            return;
        }
        streamProxyService.updateById(StreamProxy.builder().id(id).enable(flag.getValue()).build());
    }

    @Override
    public int updateStatus(String app, String stream,String mediaServerId, boolean status) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[拉流信息] 未获取到 app:{},stream:{} ",app,stream);
            return ConstEnum.Flag.NO.getValue();
        }
        boolean update = streamProxyService.update(new LambdaUpdateWrapper<StreamProxy>().set(StreamProxy::getStatus, status?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue()).set(StringUtils.isNotEmpty(mediaServerId),StreamProxy::getMediaServerId,mediaServerId).eq(StreamProxy::getApp, app).eq(StreamProxy::getStream, stream));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public int updateStatus(String mediaServerId, boolean status) {
        if(StringUtils.isEmpty(mediaServerId)){
            log.info("[拉流信息] 未获取到 mediaServerId 信息");
            return ConstEnum.Flag.NO.getValue();
        }
        boolean update = streamProxyService.update(new LambdaUpdateWrapper<StreamProxy>().set(StreamProxy::getStatus, status?ConstEnum.Flag.YES.getValue():ConstEnum.Flag.NO.getValue()).eq(StreamProxy::getMediaServerId, mediaServerId));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public List<StreamProxyVo> findAutoRemoveMediaServerIdList(String mediaServerId) {
        if(StringUtils.isEmpty(mediaServerId)){
            log.info("[拉流信息] 未获取到 mediaServerId 信息");
            return new ArrayList<>();
        }
        List<StreamProxy> list = streamProxyService.list(new LambdaQueryWrapper<StreamProxy>().eq(StreamProxy::getMediaServerId, mediaServerId).eq(StreamProxy::getEnableRemoveNoneReader, ConstEnum.Flag.YES.getValue()));
        return StreamProxyConvert.INSTANCE.convertVo(list);
    }

    @Override
    public List<StreamProxyVo> findEnableInMediaServerList(String mediaServerId, boolean enable) {
        if(StringUtils.isEmpty(mediaServerId)){
            log.info("[拉流信息] 未获取到 mediaServerId 信息");
            return new ArrayList<>();
        }
        List<StreamProxy> list = streamProxyService.list(new LambdaQueryWrapper<StreamProxy>().eq(StreamProxy::getMediaServerId, mediaServerId).eq(StreamProxy::getEnable, ConstEnum.Flag.YES.getValue()));
        return StreamProxyConvert.INSTANCE.convertVo(list);
    }

    @Override
    public int del(String app, String stream) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[拉流信息] 未获取到 app:{},stream:{} ",app,stream);
            return ConstEnum.Flag.NO.getValue();
        }
        platformGbStreamService.delPlatformGbStream(app,stream);
        boolean update = streamProxyService.remove(new LambdaQueryWrapper<StreamProxy>().eq(StreamProxy::getApp, app).eq(StreamProxy::getStream, stream));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

}
