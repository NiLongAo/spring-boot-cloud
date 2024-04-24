package cn.com.tzy.springbootstarterfreeswitch.handler.event;

import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EslEventName(EslEventHandler.DEFAULT_ESL_EVENT_HANDLER)
@Component
public class DefaultEslEventHandler implements EslEventHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(String addr, EslEvent event) {
        //默认未实现的实现处理类
        log.warn("当前事件未实现： 事件：{} addr：{},EventName:{}","DEFAULT_ESL_EVENT_HANDLER",addr, EslEventUtil.getEventName(event));
    }
}