package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.utils.XmlUtils;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * SIP命令类型： NOTIFY请求,这是作为上级发送订阅请求后，设备才会响应的
 */
@Log4j2
@Component
public class NotifyRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {

    @Override
    public String getMethod() {
        return Request.NOTIFY;
    }



    @Override
    public void process(RequestEvent event) {
        //回复订阅已接收
        try {
            responseAck((SIPRequest) event.getRequest(), Response.OK, null);
        }catch (SipException | InvalidArgumentException | ParseException e) {
            e.printStackTrace();
        }

        Element rootElement = getRootElement(event);
        if (rootElement == null) {
            log.error("处理NOTIFY消息时未获取到消息体,{}", event.getRequest());
            return;
        }

        try {
            String cmd = XmlUtils.getText(rootElement, "CmdType");
            log.info("接收到消息：" + cmd);
        }catch (Exception e){
            log.error("[NOTIFY请求]，消息处理异常：", e );
        }
    }

}
