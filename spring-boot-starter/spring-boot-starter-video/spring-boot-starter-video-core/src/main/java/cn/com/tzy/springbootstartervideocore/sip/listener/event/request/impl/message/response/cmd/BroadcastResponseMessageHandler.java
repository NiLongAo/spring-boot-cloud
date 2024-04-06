package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.cmd;

import cn.com.tzy.springbootstartervideobasic.enums.CmdType;
import cn.com.tzy.springbootstartervideobasic.vo.video.DeviceVo;
import cn.com.tzy.springbootstartervideobasic.vo.video.ParentPlatformVo;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.SipResponseEvent;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response.ResponseMessageHandler;
import cn.com.tzy.springbootstartervideocore.utils.XmlUtils;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Map;

/**
 * 广播回复信息
 */
@Log4j2
public class BroadcastResponseMessageHandler extends SipResponseEvent implements MessageHandler {



    public BroadcastResponseMessageHandler(ResponseMessageHandler handler){
        handler.setMessageHandler(CmdType.BROADCAST_RESPONSE.getValue(),this);
    }


    @Override
    public void handForDevice(RequestEvent evt, DeviceVo deviceVo, Element element) {
        try {
            String channelId = XmlUtils.getText(element,"DeviceID");
            // 回复200 OK
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
            // 此处是对本平台发出Broadcast指令的应答
            Map<String, Object> map = XmlUtil.xmlToMap(element);
            log.info("对本平台发出Broadcast指令的应答:"+ JSONUtil.toJsonStr(map));
        } catch (ParseException | SipException | InvalidArgumentException e) {
            log.error("[命令发送失败] 国标级联 语音喊话: {}", e.getMessage());
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatformVo parentPlatformVo, Element element) {
        //没有上级回复
    }
}
