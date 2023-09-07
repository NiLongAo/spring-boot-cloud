package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootcomm.constant.NotNullMap;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.enums.ProxyTypeEnum;
import cn.com.tzy.springbootstartervideobasic.enums.VideoStreamType;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.StreamProxyVo;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.demo.StreamInfo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.InviteStreamManager;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.PlayService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Map;

/**
 * 视频点播
 */
@Log4j2
@RestController("ApiPlayController")
@RequestMapping(value = "/api/play")
public class PlayController extends ApiController {

    @Resource
    private SIPCommander sipCommander;
    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private SipServer sipServer;
    @Resource
    private PlayService playService;

    /**
     * 开始点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    @GetMapping("/start")
    public DeferredResult<RestResult> start(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId){
        log.debug(String.format("开始点播 API调用，deviceId：%s ，channelId：%s", deviceId, channelId));
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_PLAY,deviceId,channelId);
        VideoRestResult<RestResult> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        if(deferredResultHolder.exist(key,null)){
            return result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if (deviceVo == null) {
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",deviceId)));
            return result;
        }
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findMediaServerForMinimumLoad(deviceVo);
        if (mediaServerVo == null) {
            deferredResultHolder.invokeAllResult(key,RestResult.result(RespCode.CODE_2.getValue(),"流媒体未找到"));
            return result;
        }
        playService.play(sipServer,mediaServerVo,deviceVo.getDeviceId(),channelId,null,(code,msg,data)->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(code,msg,data));
        });
        return result;
    }

    /**
     * 停止点播
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @return
     */
    @GetMapping("/stop")
    public DeferredResult<RestResult> stop(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId){

        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s",DeferredResultHolder.CALLBACK_CMD_STOP,deviceId,channelId);
        VideoRestResult<RestResult> result = new VideoRestResult<>(30000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"请求超时"));
        deferredResultHolder.put(key,uuid,result);

        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if (deviceVo == null) {
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",deviceId)));
            return result;
        }
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByDeviceAndChannel(VideoStreamType.play, deviceId, channelId);
        if(inviteInfo == null || inviteInfo.getStreamInfo() == null){
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"点播未找到"));
            return result;
        }
        try {
            sipCommander.streamByeCmd(sipServer,deviceVo, channelId, inviteInfo.getStream(), null,null,null,(error)->{
                deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"停止点播失败"));
            });
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            log.error("停止点播 发送bye失败：",e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"停止点播失败"));
            return result;
        }
        return result;
    }

    /**
     * 视频流转码 （非h264 转为 h264）
     * @param streamId 视频流ID
     * @return
     */
    @GetMapping("/convert")
    public RestResult<?> convert(@RequestParam("streamId") String streamId){
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByStream(null, streamId);
        if(inviteInfo == null || inviteInfo.getStreamInfo() == null){
            log.warn("视频转码API调用失败！, 视频流已停止推流!");
            return RestResult.result(RespCode.CODE_2.getValue(),"点播未找到");
        }
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findOnLineMediaServerId(inviteInfo.getStreamInfo().getMediaServerId());
        if (mediaServerVo == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"流媒体未找到");
        }
        MediaRestResult result = MediaClient.getRtpInfo(mediaServerVo, streamId);
        if(!result.getExist()){
            log.warn("视频转码API调用失败！, 视频流已停止推流!");
            return RestResult.result(RespCode.CODE_2.getValue(),"未找到视频流信息, 视频流可能已停止推流");
        }
        StreamProxyVo build = StreamProxyVo.builder()
                .type(ProxyTypeEnum.FFMPEG.getValue())
                .srcUrl(String.format("rtsp://%s:%s/rtp/%s", "127.0.0.1", mediaServerVo.getRtspPort(), streamId))
                .dstUrl(String.format("rtmp://%s:%s/convert/%s", "127.0.0.1", mediaServerVo.getRtmpPort(), streamId))
                .timeoutMs(20000)
                .enableMp4(ConstEnum.Flag.NO.getValue())
                .ffmpegCmdKey(null)
                .build();
        result = MediaClient.addStreamProxyToZlm(mediaServerVo, build);
        if(result == null ||result.getCode() != RespCode.CODE_0.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"转码失败");
        }
        Map<String, Object> data = BeanUtil.beanToMap(result.getData());
        String key = MapUtil.getStr(data, "key");
        StreamInfo info = MediaClient.getStreamInfo(mediaServerVo, "convert", streamId, null, null);
        return RestResult.result(RespCode.CODE_0.getValue(),"转码成功",new NotNullMap(){{
            putString("key",key);
            put("streamInfo",info);
        }});
    }

    /**
     * 结束转码
     * @param key 视频流key
     * @param mediaServerId 流媒体服务ID
     * @return
     */
    @GetMapping("/convert_stop")
    public RestResult<?> convertStop(@RequestParam("key") String key, @RequestParam("mediaServerId")String mediaServerId){
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findOnLineMediaServerId(mediaServerId);
        if (mediaServerVo == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"流媒体未找到");
        }
        MediaRestResult result = MediaClient.delFfmpegSource(mediaServerVo, key);
        if(result == null ||result.getCode() != RespCode.CODE_0.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"结束转码失败");
        }
        Map<String, Object> data = BeanUtil.beanToMap(result.getData());
        Boolean flag = MapUtil.getBool(data, "flag",Boolean.FALSE);
        if(!flag){
            return RestResult.result(RespCode.CODE_2.getValue(),"已结束转码");
        }

        return RestResult.result(RespCode.CODE_0.getValue(),"结束转码成功");
    }
}
