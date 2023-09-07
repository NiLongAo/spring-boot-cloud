package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.enums.VideoStreamType;
import cn.com.tzy.springbootstartervideobasic.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideocore.demo.InviteInfo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.InviteStreamManager;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.PlayService;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
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

/**
 * 视频回放
 */
@Log4j2
@RestController("ApiPlaybackController")
@RequestMapping(value = "/api/playback")
public class PlaybackController extends ApiController {

    @Resource
    private SIPCommander sipCommander;

    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private SipServer sipServer;
    @Resource
    private PlayService playService;


    /**
     * 播放视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @GetMapping("/start")
    public DeferredResult<RestResult> start(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    ){
        log.debug(String.format("设备回放 API调用，deviceId：%s ，channelId：%s", deviceId, channelId));
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s_%s_%s_%s",deviceId,channelId,startTime,endTime);
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
        playService.playBack(sipServer,mediaServerVo,deviceVo, channelId,null, startTime, endTime, (code,msg,data)->{
            deferredResultHolder.invokeAllResult(key,RestResult.result(code,msg,data));
        });
        return result;
    }


    /**
     * 停止视频回放
     * @param deviceId 设备国标编号
     * @param channelId 通道国标编号
     * @param streamId 流ID
     * @return
     */
    @GetMapping("/stop")
    public RestResult<?> stop(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("channelId") String channelId,
            @RequestParam("streamId") String streamId
    ){
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if (deviceVo == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",deviceId));
        }
        try {
            sipCommander.streamByeCmd(sipServer,deviceVo, channelId, streamId, null,null,null,null);
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            log.error("发送bye失败：",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"停止视频回放失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"停止视频回放成功");
    }

    /**
     * 暂停视频回放
     * @param streamId 流ID
     * @return
     */
    @GetMapping("/suspend")
    public RestResult<?> suspend(@RequestParam("streamId") String streamId){
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo = inviteStreamManager.getInviteInfoByStream(VideoStreamType.playback, streamId);
        if (inviteInfo == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"streamId不存在");
        }
        inviteInfo.getStreamInfo().setPause(true);
        inviteStreamManager.updateInviteInfo(inviteInfo);

        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findOnLineMediaServerId(inviteInfo.getStreamInfo().getMediaServerId());
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"mediaServer不存在");
        }
        MediaRestResult result = MediaClient.pauseRtpCheck(mediaServerVo, streamId);
        if(result == null || result.getCode() !=RespCode.CODE_0.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"暂停RTP接收失败");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(inviteInfo.getDeviceId());
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.playPauseCmd(sipServer,deviceVo, inviteInfo.getStreamInfo(), null,null);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[暂停视频回放] 错误：",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"暂停视频回放失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"暂停视频回放成功");
    }

    /**
     * 暂停回放恢复
     * @param streamId 流ID
     * @return
     */
    @GetMapping("/restore")
    public RestResult<?> restore(@RequestParam("streamId") String streamId){
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo =  inviteStreamManager.getInviteInfoByStream(VideoStreamType.playback,streamId);
        if (inviteInfo == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"streamId不存在");
        }
        inviteInfo.getStreamInfo().setPause(false);
        inviteStreamManager.updateInviteInfo(inviteInfo);
        MediaServerVo mediaServerVo = VideoService.getMediaServerService().findOnLineMediaServerId(inviteInfo.getStreamInfo().getMediaServerId());
        if(mediaServerVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"mediaServer不存在");
        }
        MediaRestResult result = MediaClient.resumeRtpCheck(mediaServerVo, streamId);
        if(result == null || result.getCode() !=RespCode.CODE_0.getValue()){
            return RestResult.result(RespCode.CODE_2.getValue(),"恢复RTP接收失败");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(inviteInfo.getDeviceId());
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.playResumeCmd(sipServer,deviceVo, inviteInfo.getStreamInfo(), null,null);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[暂停回放恢复] 错误：",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"暂停回放恢复失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"暂停回放恢复成功");
    }

    /**
     * 回放拖动播放
     * @param streamId 流ID
     * @param seekTime 拖动偏移量，单位s
     * @return
     */
    @GetMapping("/seek")
    public RestResult<?> seek(@RequestParam("streamId") String streamId,@RequestParam(value = "seekTime",defaultValue = "0")Long seekTime){
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo =  inviteStreamManager.getInviteInfoByStream(VideoStreamType.playback,streamId);
        if (inviteInfo == null || inviteInfo.getStreamInfo() == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"streamId不存在");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(inviteInfo.getDeviceId());
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.playSeekCmd(sipServer,deviceVo, inviteInfo.getStreamInfo(),seekTime, null,null);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[回放拖动播放] 错误：",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"回放拖动播放失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"回放拖动播放成功");
    }

    /**
     * 回放倍速播放
     * @param streamId 流ID
     * @param speed 倍速0.25 0.5 1、2、4
     * @return
     */
    @GetMapping("/speed")
    public RestResult<?> speed(@RequestParam("streamId") String streamId,@RequestParam(value = "speed",defaultValue = "0")Double speed){
        if(speed != 0.25 && speed != 0.5 && speed != 1 && speed != 2.0 && speed != 4.0) {
            return RestResult.result(RespCode.CODE_2.getValue(),"不支持的speed（0.25 0.5 1、2、4）");
        }
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();
        InviteInfo inviteInfo =  inviteStreamManager.getInviteInfoByStream(VideoStreamType.playback,streamId);
        if (inviteInfo == null || inviteInfo.getStreamInfo() == null) {
            return RestResult.result(RespCode.CODE_2.getValue(),"streamId不存在");
        }
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(inviteInfo.getDeviceId());
        if(deviceVo == null){
            return RestResult.result(RespCode.CODE_2.getValue(),"未获取设备信息");
        }
        try {
            sipCommander.playSpeedCmd(sipServer,deviceVo, inviteInfo.getStreamInfo(),speed, null,null);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[回放拖动播放] 错误：",e);
            return RestResult.result(RespCode.CODE_2.getValue(),"回放倍速播放失败");
        }
        return RestResult.result(RespCode.CODE_0.getValue(),"回放倍速播放成功");
    }
}
