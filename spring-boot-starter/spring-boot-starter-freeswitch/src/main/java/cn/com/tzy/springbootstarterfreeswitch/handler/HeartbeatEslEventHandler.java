package cn.com.tzy.springbootstarterfreeswitch.handler;

import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EslEventName(EventNames.HEARTBEAT)
@Component
public class HeartbeatEslEventHandler implements EslEventHandler {
    /** {@inheritDoc} */
    @Override
    public void handle(String addr, EslEvent event) {
//        log.info("HeartbeatEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);
    }
}