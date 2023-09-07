package cn.com.tzy.springbootwebapi.controller.video;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.common.ZLMediaKitConstant;
import cn.com.tzy.springbootstartervideobasic.vo.media.*;
import cn.com.tzy.springbootwebapi.service.video.MediaHookService;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@Api(tags = "流媒体相关信息接口",position = 4)
@RestController("WebApiVideoMediaServerController")
@RequestMapping(value = "/webapi"+ZLMediaKitConstant.HOOK_URL_PREFIX)
public class MediaHookController extends ApiController {

    @Resource
    private MediaHookService mediaHookService;


    /**
     * 流媒体心跳回调地址
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_SERVER_KEEPALIVE)
    public NotNullMap onServerKeepalive(@RequestBody HookVo param){
        return mediaHookService.onServerKeepalive(param);
    }
    /**
     * 播放器鉴权事件，
     * rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件；
     * 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。
     * 播放rtsp流时，如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_PLAY)
    public NotNullMap onPlay(@RequestBody OnPlayHookVo param){
        return mediaHookService.onPlay(param);
    }
    /**
     * rtsp/rtmp/rtp推流鉴权事件。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_PUBLISH)
    public NotNullMap onPublish(@RequestBody OnPublishHookVo param){
        return mediaHookService.onPublish(param);
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_CHANGED)
    public NotNullMap onStreamChanged(@RequestBody OnStreamChangedHookVo param){
        return mediaHookService.onStreamChanged(param);
    }

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_NONE_READER)
    public NotNullMap onStreamNoneReader(@RequestBody OnStreamNoneReaderHookVo param){
        return mediaHookService.onStreamNoneReader(param);
    }

    /**
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_NOT_FOUND)
    public DeferredResult<NotNullMap> onStreamNotFound(@RequestBody OnStreamNotFoundHookVo param){
        return mediaHookService.onStreamNotFound(param);
    }

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感。
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_SERVER_STARTED)
    public NotNullMap onServerStarted(@RequestBody JSONObject param){
        return mediaHookService.onServerStarted(param);
    }

    /**
     * 发送rtp(startSendRtp)被动关闭时回调
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_SEND_RTP_STOPPED)
    public NotNullMap onSendRtpStopped(@RequestBody OnSendRtpStoppedHookVo param){
        return mediaHookService.onSendRtpStopped(param);
    }

    /**
     * rtpServer收流超时
     * @param param
     * @return
     */
    @PostMapping(ZLMediaKitConstant.MEDIA_HOOK_ON_RTP_SERVER_TIMEOUT)
    public NotNullMap onRtpServerTimeout(@RequestBody OnRtpServerTimeoutHookVo param){
        return mediaHookService.onRtpServerTimeout(param);
    }

}
