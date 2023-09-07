package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.cmd;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query.QueryMessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
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
public class DeviceInfoQueryMessageHandler extends SipResponseEvent implements MessageHandler {

    public DeviceInfoQueryMessageHandler(QueryMessageHandler handler){
        handler.setMessageHandler(CmdType.DEVICE_INFO_QUERY.getValue(),this);
    }
    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        //设备不会去查设备
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        log.info("[DeviceInfo查询]消息");
        FromHeader fromHeader = (FromHeader) evt.getRequest().getHeader(FromHeader.NAME);
        try {
            // 回复200 OK
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] DeviceInfo查询回复: {}", e.getMessage());
            return;
        }
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        String sn = XmlUtils.getText(element,"SN");
        String channelId = XmlUtils.getText(element, "DeviceID");
        // 查询这是通道id还是设备id
        DeviceVo deviceVo = null;
        // 如果id指向平台的国标编号，那么就是查询平台的信息
        if (!parentPlatformVo.getDeviceGbId().equals(channelId)) {
            deviceVo = deviceVoService.findDeviceInfoPlatformIdChannelId(parentPlatformVo.getServerGbId(), channelId);
            if (deviceVo ==null){
                log.error("[平台没有该通道的使用权限]:platformId"+ parentPlatformVo.getServerGbId()+"  deviceID:"+channelId);
                return;
            }
        }
        try {
            sipCommanderForPlatform.deviceInfoResponse(sipServer, parentPlatformVo, deviceVo, sn, fromHeader.getTag(),null,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 国标级联 DeviceInfo查询回复: {}", e.getMessage());
        }
    }

}
