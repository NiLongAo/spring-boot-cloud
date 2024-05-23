package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.NextCommandHandler;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 设备应答处理类
 */
@Log4j2
@Component
@EslEventName(EventNames.CHANNEL_ANSWER)
public class ChannelAnswerEventHandler implements EslEventHandler {

    @Resource
    private NextCommandHandler nextCommandHandler;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 [设备应答处理类] CHANNEL_ANSWER");
        String uniqueId = EslEventUtil.getUniqueId(event);
        Date answerTime = DateUtil.date(Long.parseLong(EslEventUtil.getEventDateTimestamp(event))/1000).toSqlDate();//接通时间（毫秒值）
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if(callInfo == null){
            log.warn("未获取应答消息");
            return;
        }
        //获取处理设备
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(uniqueId);
        if(deviceInfo == null){
            log.warn("未获取处理设备");
            return;
        }
        //接听时间也是振铃结束时间
        deviceInfo.setAnswerTime(answerTime);
        deviceInfo.setRingEndTime(answerTime);
        callInfo.setAnswerCount(callInfo.getAnswerCount() + 1);
        if (StringUtils.isBlank(callInfo.getMediaHost())) {
            callInfo.setMediaHost(addr);
        }
        //下一波执行命令
        nextCommandHandler.next(callInfo,event);
        RedisService.getCallInfoManager().put(callInfo);
    }
}