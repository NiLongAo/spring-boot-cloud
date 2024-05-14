package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.event;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.Constant;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.AnswerCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.message.AnswerCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.utils.FreeswitchUtils;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;

/**
 *  设备话机振铃
 */
@Log4j2
@Component
@EslEventName(EventNames.CHANNEL_PARK)
public class ChannelParkEventHandler implements EslEventHandler {


    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private AnswerCallHandler answerCallHandler;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("进入事件 CHANNEL_PARK");
        String hangup = event.getEventHeaders().get("variable_sip_hangup_phrase");//是否挂机,OK
        if (Constant.OK.equals(hangup)) {
            return;
        }
        log.info("channel park:{}", event);
        String uniqueId = EslEventUtil.getUniqueId(event);
        String direction = EslEventUtil.getCallerDirection(event);
        String sipPort = event.getEventHeaders().get("variable_sip_via_port");//硬话机发起呼叫时携带
        String sipContactPort = event.getEventHeaders().get("variable_sip_contact_port");
        String sipProtocol = event.getEventHeaders().get("variable_sip_via_protocol");//sip信令协议
        Date answerTime = DateUtil.date(Long.parseLong(EslEventUtil.getEventDateTimestamp(event)));//接通时间（毫秒值）

        CallInfo callInfo = RedisService.getCallInfoManager().findDeviceId(uniqueId);
        if (callInfo == null && DirectionEnum.INBOUND.name().equals(direction.toUpperCase())) {
            if (sipPort == null) {
                //呼入
                inboundCall(addr,uniqueId,event);
                return;
            }
            if (!sipContactPort.equals(sipPort)) {
                //硬话机外呼
                sipOutboundCall(addr,uniqueId,event);
                return;
            }
            return;
        }
        if (callInfo == null || callInfo.getHangupDir() != null) {
            return;
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(uniqueId);
        if (deviceInfo == null) {
            return;
        }
        if (hangup != null) {
            return;
        }
        if (deviceInfo.getAnswerTime() != null && deviceInfo.getState() != null) {
            return;
        }

        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(deviceInfo.getAgentKey());
        if (agentVoInfo == null) {
            return;
        }
        CallMessage ringEntity = new CallMessage();
        ringEntity.setCaller(callInfo.getCaller());
        ringEntity.setCalled(callInfo.getCalled());
        ringEntity.setGroupId(callInfo.getGroupId());
        ringEntity.setCallId(callInfo.getCallId());
        if (deviceInfo.getState() != null) {
            switch (deviceInfo.getState()) {
                case "HOLD":
                    ringEntity.setAgentState(AgentStateEnum.HOLD);
                    agentVoInfo.setAgentState(AgentStateEnum.HOLD);
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.HOLD, agentVoInfo,ringEntity);
                case "INSERT":
                    ringEntity.setAgentState(AgentStateEnum.INSERT);
                    agentVoInfo.setAgentState(AgentStateEnum.INSERT);
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.OUT_CALLER_RING, agentVoInfo,ringEntity);
                case "LISTEN":
                    ringEntity.setAgentState(AgentStateEnum.LISTEN);
                    agentVoInfo.setAgentState(AgentStateEnum.LISTEN);
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.OUT_CALLER_RING, agentVoInfo,ringEntity);
                default:
                    break;
            }
            return;
        }
        if (deviceInfo.getRingStartTime() != null) {
            return;
        }
        deviceInfo.setRingStartTime(answerTime);
        DirectionEnum directionEnum = callInfo.getDirection();
        log.info("callId:{} device:{} park deviceType:{} cdrType:{} direction:{} sipProtocol:{}", deviceInfo.getCallId(), deviceInfo.getDeviceId(), deviceInfo.getDeviceType(), deviceInfo.getCdrType(), direction, sipProtocol);

        ringEntity.setDirection(callInfo.getDirection());
        if (directionEnum.equals(DirectionEnum.OUTBOUND)) {
            outboundCall(callInfo, deviceInfo, agentVoInfo, ringEntity);
            return;
        } else if (directionEnum.equals(DirectionEnum.INBOUND)) {
            //呼入振铃
            agentVoInfo.setBeforeState(agentVoInfo.getAgentState());
            agentVoInfo.setBeforeTime(agentVoInfo.getStateTime());
            agentVoInfo.setStateTime(Instant.now().getEpochSecond());
            agentVoInfo.setAgentState(AgentStateEnum.IN_CALL_RING);
            ringEntity.setAgentState(AgentStateEnum.IN_CALL_RING);
            if (agentVoInfo.getHiddenCustomer() == 1) {
                ringEntity.setCaller(ringEntity.getCaller());
            }
            FsService.getSendAgentMessage().sendMessage(AgentStateEnum.IN_CALL_RING, agentVoInfo,ringEntity);
        }
    }

    /**
     * 呼入电话
     *
     * @param event
     */
    private void inboundCall(String addr,String deviceId,EslEvent event) {
        String callId = String.valueOf(FreeswitchUtils.snowflake.nextId());
        String localMediaIp = event.getEventHeaders().get("variable_local_media_ip");//fs 服务地址
        String caller = event.getEventHeaders().get("variable_sip_from_user");//主叫号码
        String called = event.getEventHeaders().get("Caller-Destination-Number");//被叫号码
        String contactUri = event.getEventHeaders().get("variable_sip_contact_uri");
        //客户号码归属地
        String numberLocaton = "";
        log.info("inbount callId:{} park, caller:{}, called:{}, deviceId:{}, uri:{}", callId, caller, called, deviceId, contactUri);
        VdnPhone vdnPhone = RedisService.getVdnPhoneManager().get(called);
        if (vdnPhone == null) {
            log.error("inbount callId:{} called:{} is not match for vdn", callId, called);
            CallLogInfo callLog = new CallLogInfo();
            callLog.setCallId(callId);
            callLog.setCreateTime(new Date());
            callLog.setUpdateTime(new Date());
            callLog.setEndTime(new Date());
            callLog.setCaller(caller);
            callLog.setCalled(called);
            callLog.setNumberLocation(numberLocaton);
            callLog.setCallType(CallTypeEunm.INBOUND_CALL.name());
            callLog.setMediaHost(addr);
            callLog.setAnswerCount(1);
            callLog.setAnswerFlag(3);
            callLog.setDirection(DirectionEnum.INBOUND.name());
            callLog.setHangupDir(3);
            callLog.setHangupCode(CauseEnums.VDN_ERROR.getHuangupCode());
            callLog.setMonthTime(DateUtil.format(callLog.getCreateTime(), DatePattern.SIMPLE_MONTH_PATTERN));
            FsService.getCallCdrService().saveOrUpdateCallLog(callLog);
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(addr).deviceId(deviceId).build());
            return;
        }

        CallInfo callInfo = CallInfo.builder()
                .callId(callId)
                .callType(CallTypeEunm.INBOUND_CALL)
                .direction(DirectionEnum.INBOUND)
                .callTime(new Date())
                .numberLocation(numberLocaton)
                //接入号码
                .callerDisplay(called)
                .companyId(vdnPhone.getCompanyId())
                .mediaHost(addr)
                .ctiHost(localMediaIp)
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .callId(callId)
                .deviceId(deviceId)
                .caller(caller)
                .called(called)
                .callerLocation(numberLocaton)
                .callTime(callInfo.getCallTime())
                .deviceType(2)
                .cdrType(1)
                .build();

        CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(vdnPhone.getCompanyId());
        callInfo.setHiddenCustomer(companyInfo.getHiddenCustomer());
        callInfo.setCdrNotifyUrl(companyInfo.getNotifyUrl());
        callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
        callInfo.getDeviceList().add(deviceId);
        callInfo.getNextCommands().add(new NextCommand(deviceId, NextTypeEnum.NEXT_VDN, null));

        RedisService.getCallInfoManager().put(callInfo);
        RedisService.getDeviceInfoManager().putDeviceCallId(deviceId, callId);
        answerCallHandler.handler(AnswerCallModel.builder().mediaAddr(addr).deviceId(deviceId).build());
    }


    /**
     * 外呼
     *
     * @param callInfo
     * @param deviceInfo
     * @param agentVoInfo
     * @param ringEntity
     */
    private void outboundCall(CallInfo callInfo, DeviceInfo deviceInfo, AgentVoInfo agentVoInfo, CallMessage ringEntity) {
        if (1 == deviceInfo.getDeviceType()) {
            CallTypeEunm callType = callInfo.getCallType();
            if (callType == CallTypeEunm.OUTBOUNT_CALL) {
                if (deviceInfo.getCdrType() == 4) {
                    //外呼转接坐席
                    ringEntity.setAgentState(AgentStateEnum.TRANSFER_CALL_RING);
                    if (agentVoInfo.getHiddenCustomer() == 1) {
                        ringEntity.setCalled(ringEntity.getCalled());
                    }
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.TRANSFER_CALL_RING, agentVoInfo,ringEntity);
                } else if (deviceInfo.getCdrType() == 5) {
                    //外呼转接坐席
                    ringEntity.setAgentState(AgentStateEnum.CONSULT_CALL_RING);
                    if (agentVoInfo.getHiddenCustomer() == 1) {
                        ringEntity.setCalled(ringEntity.getCalled());
                    }
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.CONSULT_CALL_RING, agentVoInfo,ringEntity);

                } else if (deviceInfo.getCdrType() == 2) {
                    //先判断坐席是否接通，如果已经接通，则返回当前接通后的状态
                    if (deviceInfo.getAnswerTime() != null) {
                        AgentStateEnum agentState = agentVoInfo.getAgentState();
                        switch (agentState) {
                            case CONSULT:
                                ringEntity.setAgentState(AgentStateEnum.TRANSFER_CALL);
                                if (agentVoInfo.getHiddenCustomer() == 1) {
                                    ringEntity.setCalled(ringEntity.getCalled());
                                }
                                FsService.getSendAgentMessage().sendMessage(AgentStateEnum.TALKING, agentVoInfo,ringEntity);
                                break;

                            case TRANSFER:

                                break;
                        }
                        return;
                    }
                    //外呼坐席振铃
                    ringEntity.setAgentState(AgentStateEnum.OUT_CALLER_RING);
                    if (agentVoInfo.getHiddenCustomer() == 1) {
                        ringEntity.setCalled(ringEntity.getCalled());
                    }
                    FsService.getSendAgentMessage().sendMessage(AgentStateEnum.OUT_CALLER_RING, agentVoInfo,ringEntity);
                }
            } else if (callType == CallTypeEunm.INNER_CALL) {
                //内呼坐席振铃
                ringEntity.setAgentState(AgentStateEnum.IN_CALL_RING);
                FsService.getSendAgentMessage().sendMessage(AgentStateEnum.IN_CALL_RING, agentVoInfo,ringEntity);
            }
        } else {
            //外呼被叫振铃
            ringEntity.setAgentState(AgentStateEnum.OUT_CALLED_RING);
            if (agentVoInfo.getHiddenCustomer() == 1) {
                ringEntity.setCalled(ringEntity.getCalled());
            }
            FsService.getSendAgentMessage().sendMessage(AgentStateEnum.OUT_CALLED_RING, agentVoInfo,ringEntity);
        }
        RedisService.getCallInfoManager().put(callInfo);
    }

    /**
     * 硬话机外呼
     *
     * @param event
     */
    private void sipOutboundCall(String addr,String deviceId,EslEvent event) {
        //通过sip号码获取绑定的坐席
        String caller = event.getEventHeaders().get("variable_sip_from_user");//主叫号码
        String called = event.getEventHeaders().get("Caller-Destination-Number");//被叫号码
        AgentVoInfo agent = RedisService.getAgentInfoManager().get(caller);
        String callId = String.valueOf(FreeswitchUtils.snowflake.nextId());
        if (agent == null || agent.getGroupId() == null) {
            log.warn("sipOutbound callId:{}  sip:{} called:{}", callId, caller, called);
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(addr).deviceId(deviceId).build());
            return;
        }
        //获取显号
        GroupInfo groupInfo = RedisService.getGroupInfoManager().get(agent.getGroupId());
        if (groupInfo == null || CollectionUtils.isEmpty(groupInfo.getCalledDisplays())) {
            log.warn("callId:{}, agent:{}, group is null", callId, agent.getAgentKey());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(addr).deviceId(deviceId).build());
            return;
        }
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agent.getAgentKey());
        if (agentVoInfo == null) {
            RedisService.getAgentInfoManager().put(agent);
        }
        log.info("sipOutbound callId:{}  sip:{} called:{}", callId, caller, called);
        String calledDisplay = groupInfo.getCalledDisplays().get(RandomUtil.randomInt(0,groupInfo.getCalledDisplays().size()-1));

        CallInfo callInfo = CallInfo.builder()
                .callId(callId)
                .callType(CallTypeEunm.SIP_OUTBOUND_CALL)
                .direction(DirectionEnum.OUTBOUND)
                .callTime(new Date())
                .caller(caller)
                .called(called)
                .companyId(agent.getCompanyId())
                .mediaHost(addr)
                .callerDisplay(agent.getAgentId())
                .calledDisplay(calledDisplay)
                .groupId(groupInfo.getId())
                .agentKey(agent.getAgentKey())
                .build();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .callId(callId)
                .deviceId(deviceId)
                .agentKey(agent.getAgentKey())
                .caller(caller)
                .called(called)
                .callTime(callInfo.getCallTime())
                .deviceType(1)
                .cdrType(2)
                .build();

        CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(agent.getCompanyId());
        callInfo.setHiddenCustomer(companyInfo.getHiddenCustomer());
        callInfo.setCdrNotifyUrl(companyInfo.getNotifyUrl());
        callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
        callInfo.getDeviceList().add(deviceId);

        callInfo.getNextCommands().add(new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_CALL_OTHER, null));
        RedisService.getCallInfoManager().put(callInfo);
        RedisService.getDeviceInfoManager().putDeviceCallId(deviceId, callId);
        answerCallHandler.handler(AnswerCallModel.builder().mediaAddr(addr).deviceId(deviceId).build());
    }
}
