package cn.com.tzy.springbootstartervideocore.config.runner;

import cn.com.tzy.springbootstartervideobasic.vo.media.MediaRestResult;
import cn.com.tzy.springbootstartervideobasic.vo.sip.SendRtp;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.MediaServerVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.media.client.MediaClient;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstartervideocore.redis.RedisService;
import cn.com.tzy.springbootstartervideocore.redis.impl.SendRtpManager;
import cn.com.tzy.springbootstartervideocore.redis.impl.SsrcConfigManager;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.service.video.MediaServerVoService;
import cn.com.tzy.springbootstartervideocore.service.video.ParentPlatformVoService;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommanderForPlatform;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务器启动设备注册
 */
@Log4j2
@Order(40)
public class SipDeviceRunner implements CommandLineRunner {

    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private SipServer sipServer;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;

    @Override
    public void run(String... args) throws Exception {
        MediaServerVoService mediaServerVoService = VideoService.getMediaServerService();
        ParentPlatformVoService parentPlatformVoService = VideoService.getParentPlatformService();
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        // 设备离线定时任务
        dynamicTask.startCron("device-offline-5m", 300,()->{
            DeviceVoService deviceVoService = VideoService.getDeviceService();
            List<DeviceVo> allDeviceVo = deviceVoService.getAllOnlineDevice();
            for (DeviceVo deviceVo : allDeviceVo) {
                if(deviceVo.keepalive()){
                    deviceVoService.offline(deviceVo.getDeviceId());
                }
            }
        });
        //查找有国标推流全部关闭
        List<SendRtp> sendRtpList = sendRtpManager.queryAllSendRTPServer();
        for (SendRtp sendRtp : sendRtpList) {
            MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtp.getMediaServerId());
            sendRtpManager.deleteSendRTPServer(sendRtp.getPlatformId(),sendRtp.getChannelId(),sendRtp.getStreamId(),sendRtp.getCallId());
            if(mediaServerVo != null){
                ssrcConfigManager.releaseSsrc(mediaServerVo.getId(),sendRtp.getSsrc());
                MediaRestResult result = MediaClient.stopSendRtp(mediaServerVo, "__defaultVhost__", sendRtp.getApp(), sendRtp.getStreamId(), sendRtp.getSsrc());
                if(result.getCode() == 0){
                    ParentPlatformVo parentPlatformVo = parentPlatformVoService.getParentPlatformByServerGbId(sendRtp.getPlatform());
                    if(parentPlatformVo != null){
                        sipCommanderForPlatform.streamByeCmd(sipServer, parentPlatformVo,sendRtp,null,null);
                    }
                }
            }
        }
    }
}
