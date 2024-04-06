package cn.com.tzy.springbootfs.handler;

import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EslEventName(EventNames.RE_SCHEDULE)
@Component
public class ReScheduleEslEventHandler implements EslEventHandler {

//    @Resource
//    private InboundClient inboundClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(String addr, EslEvent event) {
//        log.info("ReScheduleEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);
//        log.info("{}", inboundClient);
//        EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, "version", null);
//        log.info("{}", EslHelper.formatEslMessage(eslMessage));
    }
}