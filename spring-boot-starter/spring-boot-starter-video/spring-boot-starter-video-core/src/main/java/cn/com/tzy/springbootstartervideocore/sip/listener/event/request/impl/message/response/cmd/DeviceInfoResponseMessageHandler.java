package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.cmd;

import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideobasic.enums.CharsetType;
import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.enums.StreamModeType;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.ResponseMessageHandler;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.result.DeferredResultHolder;
import cn.com.tzy.springbootstartervideocore.service.VideoService;
import cn.com.tzy.springbootstartervideocore.service.video.DeviceVoService;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * 设备信息的回复
 */
@Log4j2
public class DeviceInfoResponseMessageHandler extends SipResponseEvent implements MessageHandler {

    @Resource
    private DeferredResultHolder deferredResultHolder;

    public DeviceInfoResponseMessageHandler(ResponseMessageHandler handler){
        handler.setMessageHandler(CmdType.DEVICE_INFO_RESPONSE.getValue(),this);
    }
    
    
    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        log.debug("接收到DeviceInfo应答消息");
        // 检查设备是否存在， 不存在则不回复
        if (deviceVo == null || deviceVo.getOnline() == 0) {
            log.warn("[接收到DeviceInfo应答消息,但是设备已经离线]：" + (deviceVo != null ? deviceVo.getDeviceId():"" ));
            return;
        }
        DeviceVoService deviceVoService = VideoService.getDeviceService();
        SIPRequest request = (SIPRequest) evt.getRequest();
        element = getRootElement(evt, CharsetType.getName(deviceVo.getCharset()));
        if (element == null) {
            log.warn("[ 接收到DeviceInfo应答消息 ] content cannot be null, {}", evt.getRequest());
            try {
                responseAck((SIPRequest) evt.getRequest(), Response.BAD_REQUEST,null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] DeviceInfo应答消息 BAD_REQUEST: {}", e.getMessage());
            }
            return;
        }
        String channelId = XmlUtils.getText(element,"DeviceID");
        String key = DeferredResultHolder.CALLBACK_CMD_DEVICEINFO + deviceVo.getDeviceId() + channelId;
        deviceVo.setName(XmlUtils.getText(element, "DeviceName"));

        deviceVo.setManufacturer(XmlUtils.getText(element, "Manufacturer"));
        deviceVo.setModel(XmlUtils.getText(element, "Model"));
        deviceVo.setFirmware(XmlUtils.getText(element, "Firmware"));
        if (ObjectUtils.isEmpty(deviceVo.getStreamMode())) {
            deviceVo.setStreamMode(StreamModeType.UDP.getValue());
        }
        deviceVoService.save(deviceVo);
        deferredResultHolder.invokeAllResult(key, deviceVo);

        try {
            // 回复200 OK
            responseAck(request, Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] DeviceInfo应答消息 200: {}", e.getMessage());
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {

    }
}
