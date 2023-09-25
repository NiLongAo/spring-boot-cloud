package cn.com.tzy.springbootfeignvideo.api.video;

import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstarterfeigncore.config.feign.FeignConfiguration;
import cn.com.tzy.springbootstartervideobasic.common.ZLMediaKitConstant;
import cn.com.tzy.springbootstartervideobasic.vo.media.*;
import cn.hutool.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

@FeignClient(value = "video-server",contextId = "video-server",path = ZLMediaKitConstant.HOOK_URL_PREFIX,configuration = FeignConfiguration.class)
public interface MediaHookServiceFeign {

    /**
     * 流媒体心跳回调地址
     * @param param
     * @return
     */
    @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_SERVER_KEEPALIVE,method = RequestMethod.POST)
    NotNullMap onServerKeepalive(@RequestBody HookVo param);
    /**
     * 播放器鉴权事件，
     * rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件；
     * 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。
     * 播放rtsp流时，如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_PLAY,method = RequestMethod.POST)
    NotNullMap onPlay(@RequestBody OnPlayHookVo param);
    /**
     * rtsp/rtmp/rtp推流鉴权事件。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_PUBLISH,method = RequestMethod.POST)
    NotNullMap onPublish(@RequestBody OnPublishHookVo param);

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_CHANGED,method = RequestMethod.POST)
    NotNullMap onStreamChanged(@RequestBody OnStreamChangedHookVo param);

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_NONE_READER,method = RequestMethod.POST)
    NotNullMap onStreamNoneReader(@RequestBody OnStreamNoneReaderHookVo param);

    /**
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_STREAM_NOT_FOUND,method = RequestMethod.POST)
    DeferredResult<NotNullMap> onStreamNotFound(@RequestBody OnStreamNotFoundHookVo param);

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感。
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_SERVER_STARTED,method = RequestMethod.POST)
    public NotNullMap onServerStarted(@RequestBody JSONObject param);

    /**
     * 发送rtp(startSendRtp)被动关闭时回调
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_SEND_RTP_STOPPED,method = RequestMethod.POST)
    NotNullMap onSendRtpStopped(@RequestBody OnSendRtpStoppedHookVo param);

    /**
     * rtpServer收流超时
     * @param param
     * @return
     */
     @RequestMapping(value =ZLMediaKitConstant.MEDIA_HOOK_ON_RTP_SERVER_TIMEOUT,method = RequestMethod.POST)
    NotNullMap onRtpServerTimeout(@RequestBody OnRtpServerTimeoutHookVo param);

}
