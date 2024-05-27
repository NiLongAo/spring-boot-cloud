package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.CallTypeEunm;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.DirectionEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.CallLogInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.message.RecordCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.StrategyHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.strategy.CallStrategyHandler;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * 桥接事件
 */
@Log4j2
@Component
@EslEventName(EventNames.CHANNEL_BRIDGE)
public class ChannelBridgeEventHandler implements EslEventHandler {

    @Value("${fs.record.path:/usr/local/freeswitch}")
    protected String recordPath;
    @Value("${fs.record.file:wav}")
    protected String recordFile;
    private StrategyHandler strategyHandler;
    public ChannelBridgeEventHandler(InboundClient inboundClient){
        strategyHandler = new CallStrategyHandler(inboundClient);
    }

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 [桥接事件] CHANNEL_BRIDGE");
        String uniqueId = EslEventUtil.getUniqueId(event);
        String otherUniqueId = event.getEventHeaders().get("Other-Leg-Unique-ID");
        Date answerTime = DateUtil.date(Long.parseLong(EslEventUtil.getEventDateTimestamp(event))/1000).toSqlDate();//接通时间（毫秒值）
        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if(callInfo == null){
            return;
        }
        if (callInfo.getAnswerTime() == null) {
            callInfo.setAnswerTime(answerTime);
            CallLogInfo callLog = new CallLogInfo();
            BeanUtils.copyProperties(callInfo, callLog);
            callLog.setDirection(callInfo.getDirection().name());
            callLog.setCreateTime(callInfo.getCallTime());
            if (callInfo.getHiddenCustomer() == 1) {
                //隐藏客户侧号码
                if (DirectionEnum.INBOUND == callInfo.getDirection()) {
                    callLog.setCaller(callInfo.getCaller());
                } else if (DirectionEnum.OUTBOUND == callInfo.getDirection()) {
                    callLog.setCalled(callInfo.getCalled());
                }
            }
            FsService.getCallCdrService().saveOrUpdateCallLog(callLog);
            RedisService.getCallInfoManager().put(callInfo);
        }
        log.info("桥接成功 callId:{}, device:{}, otherDevice:{}", callInfo.getCallId(), uniqueId, otherUniqueId);
        DeviceInfo deviceInfo1 = callInfo.getDeviceInfoMap().get(uniqueId);//主叫
        DeviceInfo deviceInfo2 = callInfo.getDeviceInfoMap().get(otherUniqueId);//被叫
        if (deviceInfo1 != null && deviceInfo1.getBridgeTime() == null) {
            deviceInfo1.setBridgeTime(answerTime);
        }
        if (deviceInfo2 != null && deviceInfo2.getBridgeTime() == null) {
            deviceInfo2.setBridgeTime(answerTime);
        }
        GroupInfo groupInfo = RedisService.getGroupInfoManager().get(callInfo.getGroupId());
        if (groupInfo.getRecordType() > 0) {
            /**
             * 手动外呼：接通录音时在此录音
             * 呼入: 在此录音
             */
            if (callInfo.getCallType() == CallTypeEunm.OUTBOUNT_CALL && groupInfo.getRecordType() == 2) {
                String record = String.format("%s/%s/%s_%s_%s.%s",recordPath,DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN),callInfo.getCallId(),callInfo.getDeviceList().get(0),Instant.now().getEpochSecond(),recordFile);
                strategyHandler.handler(RecordCallModel.builder().mediaAddr(addr).deviceId(callInfo.getDeviceList().get(0)).playPath(record).build());
                if (deviceInfo2 != null && deviceInfo2.getBridgeTime() == null) {
                    deviceInfo2.setRecord(record);
                }
            } else if (callInfo.getCallType() == CallTypeEunm.INBOUND_CALL) {
                String record = String.format("%s/%s/%s_%s_%s.%s",recordPath,DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN),callInfo.getCallId(),uniqueId,Instant.now().getEpochSecond(),recordFile);
                strategyHandler.handler(RecordCallModel.builder().mediaAddr(addr).deviceId(uniqueId).playPath(record).build());
                if (deviceInfo1 != null && deviceInfo1.getBridgeTime() == null) {
                    deviceInfo1.setRecord(record);
                }
            }
        }
        if (StringUtils.isBlank(callInfo.getAgentKey())) {
            return;
        }
        if (deviceInfo1.getCdrType() == 5) {
            sendAgentState(callInfo, deviceInfo1.getAgentKey(), AgentStateEnum.CONSULTED_TALKING);
            if (deviceInfo2.getCdrType() == 2 && deviceInfo2.getDeviceType() == 1) {
                sendAgentState(callInfo, deviceInfo2.getAgentKey(), AgentStateEnum.CONSULT_TALKING);
            }
            return;
        }
        if (deviceInfo2.getCdrType() == 5) {
            sendAgentState(callInfo, deviceInfo2.getAgentKey(), AgentStateEnum.CONSULTED_TALKING);
            if (deviceInfo1.getCdrType() == 2 && deviceInfo1.getDeviceType() == 1) {
                sendAgentState(callInfo, deviceInfo1.getAgentKey(), AgentStateEnum.CONSULT_TALKING);
            }
            return;
        }
        sendAgentState(callInfo, callInfo.getAgentKey(), AgentStateEnum.TALKING);
    }

    /**
     * 电话桥接成功，给坐席sdk推送状态 通知
     */
    private void sendAgentState(CallInfo callInfo, String agentKey, AgentStateEnum agentState) {
        CallMessage callMessage = new CallMessage();
        callMessage.setCallId(callInfo.getCallId());
        callMessage.setCallType(callInfo.getCallType());
        callMessage.setAgentState(agentState);
        callMessage.setCaller(callInfo.getCaller());
        callMessage.setCalled(callInfo.getCalled());
        callMessage.setDirection(callInfo.getDirection());
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if (agentVoInfo == null) {
            return;
        }
        if (agentVoInfo.getHiddenCustomer() == 1) {
            if (callInfo.getDirection() == DirectionEnum.OUTBOUND) {
                callMessage.setCalled(callInfo.getCalled());
            } else if (callInfo.getDirection() == DirectionEnum.INBOUND) {
                callMessage.setCaller(callInfo.getCaller());
            }
        }
        agentVoInfo.setBeforeState(agentVoInfo.getAgentState());
        agentVoInfo.setBeforeTime(agentVoInfo.getStateTime());
        agentVoInfo.setStateTime(Instant.now().getEpochSecond());
        agentVoInfo.setAgentState(agentState);
        FsService.getSendAgentMessage().sendMessage(AgentStateEnum.TALKING, agentVoInfo,callMessage);
    }
}
