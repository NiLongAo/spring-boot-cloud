package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process.VdnProcessHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *  放音结束
 */
@Log4j2
@Component
@EslEventName(EventNames.PLAYBACK_STOP)
public class PlaybackStopEventHandler implements EslEventHandler {
    @Resource
    private VdnProcessHandler vdnProcessHandler;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 PLAYBACK_STOP");
        String uniqueId = EslEventUtil.getUniqueId(event);
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if (callInfo == null) {
            return;
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(uniqueId);
        if (deviceInfo == null || deviceInfo.getEndTime() != null) {
            return;
        }
        NextCommand nextCommand = callInfo.getNextCommands().isEmpty() ? null : callInfo.getNextCommands().get(0);
        if (nextCommand == null) {
            return;
        }
        vdnProcessHandler.doNextCommand(callInfo, deviceInfo, nextCommand);
        log.info("callId:{} playstop, nextType:{}", callInfo.getCallId(), nextCommand.getNextType());
    }
}
