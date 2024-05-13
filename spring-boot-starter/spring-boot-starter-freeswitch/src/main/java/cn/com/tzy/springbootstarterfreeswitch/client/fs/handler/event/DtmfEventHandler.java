package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * 按键收号
 */
@Log4j2
@Component
@EslEventName(EventNames.DTMF)
public class DtmfEventHandler implements EslEventHandler {
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 DTMF");
        log.info("按键收号 暂未实现:{}", event.getEventHeaders());
    }
}
