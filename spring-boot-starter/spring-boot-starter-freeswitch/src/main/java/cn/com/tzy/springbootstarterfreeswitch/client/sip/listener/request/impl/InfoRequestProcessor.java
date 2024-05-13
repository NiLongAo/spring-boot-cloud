package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * 回放控制
 */
@Log4j2
@Component
public class InfoRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {


    @Override
    public String getMethod() {return Request.INFO;}


    @Override
    public void process(RequestEvent evt) {
        log.debug("接收到消息：" + evt.getRequest());
        SIPRequest request = (SIPRequest) evt.getRequest();
        // 不存在则回复404
        try {
            responseAck(request, Response.NOT_FOUND, "此消息暂不处理");
        } catch (InvalidArgumentException |SipException | ParseException e) {
            log.error("[命令发送失败] INFO NOT_FOUND: {}", e.getMessage());
        }
    }


}
