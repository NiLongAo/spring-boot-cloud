package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.PlayBackCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 执行完成时事件
 */
@Log4j2
@Component
@EslEventName(EventNames.CHANNEL_EXECUTE_COMPLETE)
public class ChannelExecuteCompleteEventHandler implements EslEventHandler {

    @Resource
    private PlayBackCallHandler playBackCallHandler;
    @Resource
    private HangupCallHandler hangupCallHandler;

    @Override
    public void handle(String addr, EslEvent event) {
        String application = event.getEventHeaders().get("Application");
        log.info("进入事件 [执行完成时事件 application:{}]  CHANNEL_EXECUTE_COMPLETE",application);
        if (StringUtils.isBlank(application)) {
            return;
        }
        String applicationData = event.getEventHeaders().get("variable_current_application_data");
        String response = event.getEventHeaders().get("Application-Response");
        String dtmf = event.getEventHeaders().get("variable_SYMWRD_DTMF_RETURN");


        String uniqueId = EslEventUtil.getUniqueId(event);
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if (callInfo == null) {
            return;
        }
        switch (application) {
            case "playback":
                if ("FILE PLAYED".equals(response)) {
                    log.info("deviceId:{}, playback:{} success", uniqueId, applicationData);
                    //正常排队放音在放完之后，需要循环放音
                    if (callInfo.getQueueStartTime() != null && callInfo.getQueueEndTime() == null) {
                        playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(uniqueId).playPath("queue.wav").build());
                        return;
                    }

                } else if ("FILE NOT FOUND".equals(response)) {
                    log.error("deviceId:{}  file:{} not found", uniqueId, applicationData);
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(uniqueId).build());
                    return;
                }
                break;

            case "play_and_get_digits":
                log.info("deviceId:{}, get dtmf:{}", uniqueId,dtmf);
                break;

            case "break":
                break;

            default:
                break;
        }
        log.debug("execute:{} data:{} resposne:{}", application, applicationData, response);
    }
}
