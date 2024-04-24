package cn.com.tzy.springbootstarterfreeswitch.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.hutool.core.date.DateUtil;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *  录音开始
 */
@Log4j2
@Component
@EslEventName(EventNames.RECORD_START)
public class RecordStartEventHandler  implements EslEventHandler {
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 RECORD_START");
        String uniqueId = EslEventUtil.getUniqueId(event);
        Date answerTime = DateUtil.date(Long.parseLong(EslEventUtil.getEventDateTimestamp(event)));//接通时间（毫秒值）
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if (callInfo == null) {
            return;
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(uniqueId);
        deviceInfo.setRecordStartTime(answerTime);
    }
}
