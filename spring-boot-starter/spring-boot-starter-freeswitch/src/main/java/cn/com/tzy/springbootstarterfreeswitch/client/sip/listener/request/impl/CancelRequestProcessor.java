package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.message.Request;

/**
 * SIP命令类型： CANCEL请求
 */
@Log4j2
@Component
public class CancelRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {
    @Override
    public String getMethod() {
        return Request.CANCEL;
    }

    /**
     * 处理CANCEL请求
     * @param event 事件
     */
    @Override
    public void process(RequestEvent event) {
        // TODO 优先级99 Cancel Request消息实现，此消息一般为级联消息，上级给下级发送请求取消指令
        log.info("CANCEL 消息，暂未处理");
    }
}
