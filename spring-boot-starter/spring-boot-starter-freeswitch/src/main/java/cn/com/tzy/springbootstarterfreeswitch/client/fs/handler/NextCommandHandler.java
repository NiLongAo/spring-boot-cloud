package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler;

import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.*;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.CallTypeEunm;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process.VdnProcessHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.message.*;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.util.EslEventUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

/**
 * 下一步处理类
 */
@Log4j2
@Component
public class NextCommandHandler {

    @Value("${fs.record.path:/usr/local/freeswitch}")
    protected String recordPath;
    @Value("${fs.record.file:wav}")
    protected String recordFile;

    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private RecordCallHandler recordCallHandler;
    @Resource
    private MakeCallHandler makeCallHandler;
    @Resource
    private TransferCallHandler transferCallHandler;
    @Resource
    private BridgeCallHandler bridgeCallHandler;
    @Resource
    private VdnProcessHandler vdnProcessHandler;

    public void next(CallInfo callInfo, EslEvent event){
        String uniqueId = EslEventUtil.getUniqueId(event);
        NextCommand nextCommand = callInfo.getNextCommands().isEmpty() ? null : callInfo.getNextCommands().get(0);
        if (nextCommand == null) {
            return;
        }
        //准备执行时删除这次处理的命令
        callInfo.getNextCommands().remove(nextCommand);
        switch (nextCommand.getNextType()) {
            case NEXT_VDN:
                //进vdn
                matchVdnCode(event, callInfo, callInfo.getDeviceInfoMap().get(uniqueId));
                break;
            case NEXT_CALL_OTHER:
                //呼叫另外一侧
                callOther(callInfo, callInfo.getDeviceInfoMap().get(uniqueId));
                break;
            case NEXT_TRANSFER_CALL:
                //转接电话 //将通话中的通道转接到另一个端
                transferCall(callInfo, nextCommand, event);
                break;
            case NEXT_CALL_BRIDGE: //正常打电话
                //桥接
                callBridge(callInfo, callInfo.getDeviceInfoMap().get(uniqueId), nextCommand, event);
                break;
            case NEXT_CONSULT_AGENT:
                //咨询坐席
                consultAgent(callInfo, callInfo.getDeviceInfoMap().get(uniqueId), nextCommand, event);
                break;
            case NEXT_CONSULT_CALLOUT:
                //咨询外线
                consultCallout(callInfo, callInfo.getDeviceInfoMap().get(uniqueId), nextCommand, event);
                break;
            //另一部分
            default:
                log.warn("can not match command :{}, callId:{}", nextCommand.getNextType(), callInfo.getCallId());
                break;
        }
    }

    /**
     * 呼入电话进入vdn
     */
    private void matchVdnCode(EslEvent event, CallInfo callInfo, DeviceInfo deviceInfo) {
        String caller = EslEventUtil.getCallerCallerIdNumber(event);//主叫号码
        String called = EslEventUtil.getCallerDestinationNumber(event);//被叫号码
        VdnPhoneInfo vdnPhoneInfo = RedisService.getVdnPhoneManager().get(callInfo.getCallerDisplay());
        log.info("inbount caller:{} called:{} for vdnId:{}", caller, called, vdnPhoneInfo.getVdnId());
        CompanyInfo companyInfo = RedisService.getCompanyInfoManager().get(vdnPhoneInfo.getCompanyId());
        if (companyInfo == null || companyInfo.getStatus() == 0) {
            log.info("vdnPhone is not match:{}  {} ", caller, called);
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
            return;
        }
        vdnProcessHandler.hanlder(callInfo, deviceInfo, vdnPhoneInfo.getVdnId());
        return;
    }

    /**
     * 呼叫另外一侧
     *
     * @param callInfo
     * @param deviceInfo
     */
    private void callOther(CallInfo callInfo, DeviceInfo deviceInfo) {
        GroupInfo groupInfo = RedisService.getGroupInfoManager().get(callInfo.getGroupId());
        if(groupInfo == null){
            log.warn("呼叫另外一侧 未获取技能组信息 CallId：{}",callInfo.getCallId());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
            return;
        } else if (groupInfo != null && groupInfo.getRecordType() == 1) {
            //振铃录音
            String record = String.format("%s/%s/%s_%s_%s.%s",recordPath,DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN),callInfo.getCallId(),deviceInfo.getDeviceId(),Instant.now().getEpochSecond(),recordFile);
            recordCallHandler.handler(RecordCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callInfo.getDeviceList().get(0)).playPath(record).build());
            deviceInfo.setRecord(record);
            deviceInfo.setRecordStartTime(deviceInfo.getAnswerTime());
            callInfo.getDeviceInfoMap().put(deviceInfo.getDeviceId(), deviceInfo);
        }
        String deviceId = RandomUtil.randomString(32);
        log.info("呼另外一侧电话: callId:{}  display:{}  called:{}  deviceId:{} ", callInfo.getCallId(), callInfo.getCalledDisplay(), callInfo.getCalled(), deviceId);
        String called = callInfo.getCalled();//被叫号码
        //坐席内呼
        if (callInfo.getCallType() == CallTypeEunm.INNER_CALL) {
            AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(called);
            called = agentVoInfo.getCalled();
            agentVoInfo.setCallId(callInfo.getCallId());
            agentVoInfo.setDeviceId(deviceId);
        }
        RouteGateWayInfo routeGateWayInfo = RedisService.getCompanyInfoManager().getRouteGateWayInfo(callInfo.getCompanyId(), called);
        if (routeGateWayInfo == null) {
            log.warn("callId:{} routeGateWayInfo error, called:{}", callInfo.getCallId(), callInfo.getCalled());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
            return;
        }
        callInfo.getDeviceList().add(deviceId);//存储被叫uuid
        DeviceInfo deviceInfo1 = new DeviceInfo();
        //1:坐席,2:客户,3:外线
        deviceInfo1.setDeviceType(callInfo.getCallType() == CallTypeEunm.INNER_CALL ? 1 : 2);
        //1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插
        deviceInfo1.setCdrType(callInfo.getCallType() == CallTypeEunm.INNER_CALL ? 3 : 2);
        deviceInfo1.setCallId(callInfo.getCallId());
        deviceInfo1.setCalled(called);
        deviceInfo1.setDisplay(callInfo.getCalledDisplay());
        deviceInfo1.setCaller(callInfo.getCalledDisplay());
        deviceInfo1.setDeviceId(deviceId);
        deviceInfo1.setCallTime(new Date());
        deviceInfo1.setAgentKey(callInfo.getAgentKey());
        callInfo.getNextCommands().add(new NextCommand(deviceInfo.getDeviceId(), NextTypeEnum.NEXT_CALL_BRIDGE, deviceInfo1.getDeviceId()));
        callInfo.getDeviceInfoMap().put(deviceId, deviceInfo1);
        RedisService.getDeviceInfoManager().putDeviceCallId(deviceId, callInfo.getCallId());

        /**
         * 呼叫外线，设置超时时间
         */
        makeCallHandler.handler(MakeCallModel.builder()
            .deviceId(deviceId)
            .callId(callInfo.getCallId())
            .display(callInfo.getCaller())
            .called(called)
            .originateTimeout(groupInfo.getCallTimeOut())
            .sipHeaderList(null)
            .gatewayModel(RouteGatewayModel.builder()
                    .mediaHost(routeGateWayInfo.getMediaHost())
                    .mediaPort(routeGateWayInfo.getMediaPort())
                    .callerPrefix(routeGateWayInfo.getCallerPrefix())
                    .calledPrefix(routeGateWayInfo.getCalledPrefix())
                    .profile(routeGateWayInfo.getProfile())
                    .sipHeaderList(Arrays.asList(routeGateWayInfo.getSipHeader1(),routeGateWayInfo.getSipHeader2(),routeGateWayInfo.getSipHeader3()))
                    .build())
        .build());
    }

    private void transferCall(CallInfo callInfo, NextCommand nextCommand, EslEvent event){
        /**
         * 转接电话 deviceInfo为被转接设备
         */
        String fromDeviceId = nextCommand.getDeviceId();
        String uniqueId = EslEventUtil.getUniqueId(event);
        callInfo.getNextCommands().add(NextCommand.builder().deviceId(uniqueId).nextType(NextTypeEnum.NEXT_TRANSFER_SUCCESS).nextValue(callInfo.getDeviceList().get(1)).build());
        transferCallHandler.handler(TransferCallModel.builder().mediaAddr(callInfo.getMediaHost()).oldDeviceId(fromDeviceId).newDeviceId(uniqueId).build());
        callInfo.getNextCommands().add(NextCommand.builder().nextType(NextTypeEnum.NORNAL).build());
    }
    /**
     * 桥接电话
     *
     * @param callInfo
     * @param deviceInfo
     * @param nextCommand
     * @param event
     */
    private void callBridge(CallInfo callInfo, DeviceInfo deviceInfo, NextCommand nextCommand, EslEvent event) {
        log.info("开始桥接电话: callId:{} caller:{} called:{} device1:{}, device2:{}", callInfo.getCallId(), callInfo.getCaller(), callInfo.getCalled(), nextCommand.getDeviceId(), nextCommand.getNextValue());
        DeviceInfo deviceInfo1 = callInfo.getDeviceInfoMap().get(nextCommand.getDeviceId());
        DeviceInfo deviceInfo2 = callInfo.getDeviceInfoMap().get(nextCommand.getNextValue());
        Date answerTime = DateUtil.date(Long.parseLong(EslEventUtil.getEventDateTimestamp(event))/1000).toSqlDate();//接通时间（毫秒值）
        if (deviceInfo1.getBridgeTime() == null) {
            deviceInfo1.setBridgeTime(answerTime);
        }
        if (deviceInfo2.getBridgeTime() == null) {
            deviceInfo2.setBridgeTime(answerTime);
        }
        bridgeCallHandler.handler(BridgeCallModel.builder().mediaAddr(callInfo.getMediaHost()).oneDeviceId(nextCommand.getDeviceId()).twoDeviceId(nextCommand.getNextValue()).build());
        /**
         * 呼入电话，坐席接听后，需要桥接
         */
        if (callInfo.getCallType() == CallTypeEunm.INBOUND_CALL) {
            if (callInfo.getQueueStartTime() != null && callInfo.getQueueEndTime() == null && deviceInfo.getDeviceType() == 1) {
                callInfo.setQueueEndTime(deviceInfo.getAnswerTime());
                if (!CollectionUtils.isEmpty(callInfo.getCallDetails())) {
                    CallDetail callDetail = callInfo.getCallDetails().get(callInfo.getCallDetails().size() - 1);
                    if (callDetail != null) {
                        callDetail.setEndTime(deviceInfo.getAnswerTime());
                    }
                }
                //更新坐席应答次数
                AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(deviceInfo.getAgentKey());
                agentVoInfo.setTotalAnswerTimes(agentVoInfo.getTotalRingTimes() + 1);
                RedisService.getAgentInfoManager().put(agentVoInfo);
            }
        }
    }

    /**
     * 咨询坐席
     *
     * @param callInfo
     * @param deviceInfo
     * @param nextCommand
     * @param event
     */
    private void consultAgent(CallInfo callInfo, DeviceInfo deviceInfo, NextCommand nextCommand, EslEvent event) {
        /**
         * 咨询坐席
         */
        GroupInfo groupInfo = RedisService.getGroupInfoManager().get(callInfo.getGroupId());
        if(groupInfo == null){
            log.warn("呼叫另外一侧 未获取技能组信息 CallId：{}",callInfo.getCallId());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
            return;
        } else if (groupInfo != null && groupInfo.getRecordType() == 1) {
            //振铃录音
            String record = String.format("%s/%s/%s_%s_%s.%s",recordPath,DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN),callInfo.getCallId(),deviceInfo.getDeviceId(),Instant.now().getEpochSecond(),recordFile);
            recordCallHandler.handler(RecordCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(callInfo.getDeviceList().get(0)).playPath(record).build());
            deviceInfo.setRecord(record);
            deviceInfo.setRecordStartTime(deviceInfo.getAnswerTime());
            callInfo.getDeviceInfoMap().put(deviceInfo.getDeviceId(), deviceInfo);
        }
        //发起咨询坐席先断开
        transferCallHandler.handler(TransferCallModel.builder().mediaAddr(callInfo.getMediaHost()).newDeviceId(nextCommand.getDeviceId()).build());
        String uniqueId = EslEventUtil.getUniqueId(event);
        log.info("开始桥接电话: callId:{} caller:{} called:{} device1:{}, device2:{}", callInfo.getCallId(), callInfo.getCaller(), callInfo.getCalled(), nextCommand.getDeviceId(), uniqueId);
        bridgeCallHandler.handler(BridgeCallModel.builder().mediaAddr(callInfo.getMediaHost()).oneDeviceId(nextCommand.getDeviceId()).twoDeviceId(uniqueId).build());
        if (StringUtils.isBlank(callInfo.getConference())) {
            //客户保持音
            recordCallHandler.handler(RecordCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(nextCommand.getNextValue()).playPath("hold.wav").build());
            DeviceInfo consultDevice = callInfo.getDeviceInfoMap().get(nextCommand.getNextValue());
            consultDevice.setState(AgentStateEnum.HOLD.name());
        }
    }
    /**
     * 咨询外线
     *
     * @param callInfo
     * @param deviceInfo
     * @param nextCommand
     * @param event
     */
    private void consultCallout(CallInfo callInfo, DeviceInfo deviceInfo, NextCommand nextCommand, EslEvent event) {

        //发起咨询坐席先断开
        transferCallHandler.handler(TransferCallModel.builder().mediaAddr(callInfo.getMediaHost()).newDeviceId(nextCommand.getDeviceId()).build());
        String uniqueId = EslEventUtil.getUniqueId(event);
        log.info("开始桥接电话: callId:{} caller:{} called:{} device1:{}, device2:{}", callInfo.getCallId(), callInfo.getCaller(), callInfo.getCalled(), nextCommand.getDeviceId(), uniqueId);
        bridgeCallHandler.handler(BridgeCallModel.builder().mediaAddr(callInfo.getMediaHost()).oneDeviceId(nextCommand.getDeviceId()).twoDeviceId(uniqueId).build());
        if (StringUtils.isBlank(callInfo.getConference())) {
            //客户保持音
            recordCallHandler.handler(RecordCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(nextCommand.getNextValue()).playPath("hold.wav").build());
            DeviceInfo consultDevice = callInfo.getDeviceInfoMap().get(nextCommand.getNextValue());
            consultDevice.setState(AgentStateEnum.HOLD.name());
        }
    }

}
