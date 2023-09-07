package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.cmd;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceChannelVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceChannelVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.QueryMessageHandler;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.FromHeader;
import javax.sip.message.Response;
import java.text.ParseException;

@Log4j2
public class DeviceStatusQueryMessageHandler extends SipResponseEvent implements MessageHandler {

    public DeviceStatusQueryMessageHandler(QueryMessageHandler handler){
        handler.setMessageHandler(CmdType.DEVICE_STATUS_QUERY.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        //设备不会去查设备状态
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        log.info("接收到DeviceStatus查询消息");
        FromHeader fromHeader = (FromHeader) evt.getRequest().getHeader(FromHeader.NAME);
        DeviceChannelVoService deviceChannelVoService = VideoService.getDeviceChannelService();
        // 回复200 OK
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 国标级联 DeviceStatus查询回复200OK: {}", e.getMessage());
        }
        String sn = XmlUtils.getText(element,"SN");
        String channelId = XmlUtils.getText(element, "DeviceID");
        DeviceChannelVo deviceChannelVo = deviceChannelVoService.findPlatformIdChannelId(parentPlatformVo.getServerGbId(), channelId);
        if (deviceChannelVo ==null){
            log.error("[平台没有该通道的使用权限]:platformId"+ parentPlatformVo.getServerGbId()+"  deviceID:"+channelId);
            return;
        }
        try {
            sipCommanderForPlatform.deviceStatusResponse(sipServer, parentPlatformVo,channelId, sn, fromHeader.getTag(), deviceChannelVo.getStatus(),null,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 国标级联 DeviceStatus查询回复: {}", e.getMessage());
        }
    }
}
