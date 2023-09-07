package cn.com.tzy.springbootvideo.config.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootentity.dome.video.StreamPush;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamPushVo;
import cn.com.tzy.springbootstartervideocore.service.video.StreamPushVoService;
import cn.com.tzy.springbootvideo.convert.video.StreamPushConvert;
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
public class StreamPushVoServiceImpl extends StreamPushVoService {

    @Resource
    private cn.com.tzy.springbootvideo.service.StreamPushService streamPushService;
    @Resource
    private PlatformGbStreamService platformGbStreamService;

    @Override
    public StreamPushVo findAppStream(String app, String stream) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",app,stream);
            return null;
        }
        StreamPush one = streamPushService.getOne(new LambdaQueryWrapper<StreamPush>().eq(StreamPush::getApp, app).eq(StreamPush::getStream, stream));
        return StreamPushConvert.INSTANCE.convert(one);
    }

    @Override
    public List<StreamPushVo> findMediaServiceNotGbId(String mediaServiceId) {
        if(StringUtils.isEmpty(mediaServiceId)){
            log.info("[推流信息] 未获取到 mediaServerId 信息");
            return new ArrayList<>();
        }
        List<StreamPush> list = streamPushService.findMediaServiceNotGbId(mediaServiceId);
        return StreamPushConvert.INSTANCE.convertVo(list);
    }

    @Override
    public int updateStreamPush(StreamPushVo param) {
        if(StringUtils.isEmpty(param.getApp()) || StringUtils.isEmpty(param.getStream())){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",param.getApp(),param.getStream());
            return ConstEnum.Flag.NO.getValue();
        }

        StreamPush convert = StreamPushConvert.INSTANCE.convert(param);
        boolean update = streamPushService.update(convert, new LambdaQueryWrapper<StreamPush>().eq(StreamPush::getApp, param.getApp()).eq(StreamPush::getStream, param.getStream()));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public int insert(StreamPushVo param) {
        if(StringUtils.isEmpty(param.getApp()) || StringUtils.isEmpty(param.getStream())){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",param.getApp(),param.getStream());
            return ConstEnum.Flag.NO.getValue();
        }

        StreamPush convert = StreamPushConvert.INSTANCE.convert(param);
        boolean save = streamPushService.save(convert);
        return save ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public int update(StreamPushVo param) {
        if(StringUtils.isEmpty(param.getApp()) || StringUtils.isEmpty(param.getStream())){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",param.getApp(),param.getStream());
            return ConstEnum.Flag.NO.getValue();
        }

        StreamPush convert = StreamPushConvert.INSTANCE.convert(param);
        boolean update = streamPushService.update(convert,new LambdaQueryWrapper<StreamPush>().eq(StreamPush::getApp, param.getApp()).eq(StreamPush::getStream, param.getStream()));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public int updateStatus(String mediaServiceId, boolean status) {
        if(StringUtils.isEmpty(mediaServiceId)){
            log.info("[推流信息] 未获取到 mediaServerId 信息");
            return ConstEnum.Flag.NO.getValue();
        }
        boolean update = streamPushService.update(new LambdaUpdateWrapper<StreamPush>().set(StreamPush::getStatus, status ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue()).eq(StreamPush::getMediaServerId, mediaServiceId));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }

    @Override
    public int updateStatus(String app, String stream, boolean status) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",app,stream);
            return ConstEnum.Flag.NO.getValue();
        }
        boolean update = streamPushService.update(new LambdaUpdateWrapper<StreamPush>().set(StreamPush::getStatus, status ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue()).eq(StreamPush::getApp, app).eq(StreamPush::getStream,stream));
        return update ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }


    @Override
    public int del(String app, String stream) {
        if(StringUtils.isEmpty(app) || StringUtils.isEmpty(stream)){
            log.info("[推流信息] 未获取到 app:{},stream:{} ",app,stream);
            return ConstEnum.Flag.NO.getValue();
        }
        platformGbStreamService.delPlatformGbStream(app,stream);
        boolean del = streamPushService.remove(new LambdaQueryWrapper<StreamPush>().eq(StreamPush::getApp, app).eq(StreamPush::getStream,stream));
        return del ? ConstEnum.Flag.YES.getValue() : ConstEnum.Flag.NO.getValue();
    }
}
