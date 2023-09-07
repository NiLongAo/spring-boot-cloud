package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstartervideobasic.vo.media.OnStreamChangedHookVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.GbStreamVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamPushVo;
import cn.com.tzy.springbootstartervideocore.service.VideoService;

import java.util.List;

public abstract class StreamPushVoService {

    public abstract StreamPushVo findAppStream(String app, String stream);

    public abstract List<StreamPushVo> findMediaServiceNotGbId(String mediaServiceId);

    public abstract int insert(StreamPushVo vo);

    public abstract int update(StreamPushVo vo);

    public abstract int updateStreamPush(StreamPushVo param);

    public abstract int updateStatus(String mediaServiceId,boolean status);

    public abstract int updateStatus(String app, String stream,boolean status);

    public abstract int del(String app, String stream);

    public void addPush(OnStreamChangedHookVo hookVo){
        GbStreamVoService gbStreamVoService = VideoService.getGbStreamService();
        StreamPushVo transform = new StreamPushVo().transform(hookVo);
        StreamPushVo appStream = findAppStream(transform.getApp(), transform.getStream());
        if(appStream == null){
            insert(transform);
        }else {
            update(transform);
            gbStreamVoService.update(GbStreamVo.builder().app(hookVo.getApp()).stream(hookVo.getStream()).mediaServerId(hookVo.getMediaServerId()).build());
        }
    }

    public void removePush(String app, String stream){
        GbStreamVoService gbStreamVoService = VideoService.getGbStreamService();
        //存在说明手动加的
        //手动加的只离线
        GbStreamVo appStream = gbStreamVoService.findAppStream(app, stream);
        if(appStream == null){
            del(app,stream);
        }else {
            update(StreamPushVo.builder().app(app).stream(stream).pushIng(ConstEnum.Flag.NO.getValue()).build());
        }
    }
}
