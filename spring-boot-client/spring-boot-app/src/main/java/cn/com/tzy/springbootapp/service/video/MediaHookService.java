package cn.com.tzy.springbootapp.service.video;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootfeignvideo.api.video.MediaHookServiceFeign;
import cn.com.tzy.springbootstartervideobasic.vo.media.*;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@Service
public class MediaHookService {

    @Resource
    private MediaHookServiceFeign mediaHookServiceFeign;
    /**
     * 流媒体心跳回调地址
     * @param param
     * @return
     */
    public NotNullMap onServerKeepalive(HookVo param){
        return mediaHookServiceFeign.onServerKeepalive(param);
    }
    /**
     * 播放器鉴权事件，
     * rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件；
     * 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。
     * 播放rtsp流时，如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件。
     * @param param
     * @return
     */
    public NotNullMap onPlay(OnPlayHookVo param){
        return mediaHookServiceFeign.onPlay(param);
    }
    /**
     * rtsp/rtmp/rtp推流鉴权事件。
     * @param param
     * @return
     */
    public NotNullMap onPublish(OnPublishHookVo param){
        return mediaHookServiceFeign.onPublish(param);
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感。
     * @param param
     * @return
     */
    public NotNullMap onStreamChanged(OnStreamChangedHookVo param){
        return mediaHookServiceFeign.onStreamChanged(param);
    }

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。
     * @param param
     * @return
     */
    public NotNullMap onStreamNoneReader(OnStreamNoneReaderHookVo param){
        return mediaHookServiceFeign.onStreamNoneReader(param);
    }

    /**
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感。
     * @param param
     * @return
     */
    public DeferredResult<NotNullMap> onStreamNotFound(OnStreamNotFoundHookVo param){
        return mediaHookServiceFeign.onStreamNotFound(param);
    }

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感。
     * @param param
     * @return
     */
    public NotNullMap onServerStarted(JSONObject param){
        return mediaHookServiceFeign.onServerStarted(param);
    }

    /**
     * 发送rtp(startSendRtp)被动关闭时回调
     * @param param
     * @return
     */
    public NotNullMap onSendRtpStopped(OnSendRtpStoppedHookVo param){
        return mediaHookServiceFeign.onSendRtpStopped(param);
    }

    /**
     * rtpServer收流超时
     * @param param
     * @return
     */
    public NotNullMap onRtpServerTimeout(OnRtpServerTimeoutHookVo param){
        return mediaHookServiceFeign.onRtpServerTimeout(param);
    }


}
