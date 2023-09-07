package cn.com.tzy.springbootvideo.controller.api;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideocore.demo.VideoRestResult;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
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
 * 语音推流等相关接口
 */
@Log4j2
@RestController("ApiAudioPushController")
@RequestMapping(value = "/api/audio/push")
public class AudioPushController extends ApiController {

    @Resource
    private DeferredResultHolder deferredResultHolder;
    @Resource
    private SipServer sipServer;
    @Resource
    private SIPCommander sipCommander;


    /**
     * 获取语音对讲推流地址
     */
    @GetMapping("audio_push_path")
    public RestResult<?> findAudioPushPath(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId) {
        return  VideoService.getDeviceChannelService().findAudioPushPath(deviceId,channelId);
    }

    /**
     * 语音广播命令
     * @param deviceId 设备国标编号
     * @return
     */
    @GetMapping("/broadcast")
    public DeferredResult<RestResult> broadcast(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId){
        log.debug("语音广播API调用");
        String uuid = RandomUtil.randomString(32);
        String key = String.format("%s%s_%s", DeferredResultHolder.CALLBACK_CMD_BROADCAST,deviceId,channelId);
        VideoRestResult<RestResult> result = new VideoRestResult<>(5000L,()-> RestResult.result(RespCode.CODE_2.getValue(),"设备不支持"));
        if (deferredResultHolder.exist(key, null)) {
            return result;
        }
        deferredResultHolder.put(key,uuid,result);
        DeviceVo deviceVo = VideoService.getDeviceService().findDeviceGbId(deviceId);
        if (deviceVo == null) {
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),String.format("设备：%s 未找到",deviceId)));
            return result;
        }
        try {
            sipCommander.audioBroadcastCmd(sipServer,deviceVo,channelId,null,null);
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("语音广播命令 指令错误：",e);
            deferredResultHolder.invokeResult(key,uuid,RestResult.result(RespCode.CODE_2.getValue(),"语音广播命令失败"));
        }
        return result;
    }

    /**
     * 获取语音对讲推流状态
     */
    @GetMapping("audio_push_status")
    public RestResult<?> findAudioPushStatus(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId) {
        return  VideoService.getDeviceChannelService().findAudioPushStatus(deviceId,channelId);
    }


    /**
     * 停止推流
     */
    @GetMapping("stop_audio_push")
    public RestResult<?> stopAudioPushStatus(@RequestParam("deviceId") String deviceId, @RequestParam("channelId")String channelId) {
        return  VideoService.getDeviceChannelService().stopAudioPushStatus(deviceId,channelId);
    }

}
