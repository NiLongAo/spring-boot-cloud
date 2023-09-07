package cn.com.tzy.springbootstartervideocore.service.video;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceMobilePositionVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.demo.NotifySubscribeInfo;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommanderForPlatform;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

@Log4j2
public abstract class DeviceMobilePositionVoService {

    @Resource
    protected SipServer sipServer;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    public abstract void save(DeviceMobilePositionVo deviceMobilePositionVo);

    public abstract DeviceMobilePositionVo findChannelId(String channelId);
    /**
     * 发送位置订阅通知
     * @param platformId
     * @param info
     */
   public void sendNotifyMobilePosition(String platformId, NotifySubscribeInfo info){
       ParentPlatformVo platformVo = VideoService.getParentPlatformService().getParentPlatformByServerGbId(platformId);
       if (platformVo == null || platformVo.getStatus() == ConstEnum.Flag.NO.getValue()) {
           return;
       }
       DeviceMobilePositionVo deviceMobilePositionVo = findChannelId(info.getId());
       if(deviceMobilePositionVo == null){
           log.info("[发送设备位置通知]： 未获取位置信息 channelId ：{}",info.getId());
           return;
       }
       try {
           sipCommanderForPlatform.sendNotifyMobilePosition(sipServer,platformVo,deviceMobilePositionVo,null,null);
       } catch (InvalidArgumentException |ParseException | NoSuchFieldException | SipException | IllegalAccessException e) {
           log.error("[发送设备位置通知]： 发送SIP消息失败 channelId ：{}:",info.getId(),e);
       }

   }


}
