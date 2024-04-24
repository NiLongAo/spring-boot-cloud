package cn.com.tzy.springbootstarterfreeswitch.handler.process;

import cn.com.tzy.springbootstarterfreeswitch.enums.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.handler.message.MakeCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.MakeCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.RouteGatewayModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

/**
 * 转接座席
 */
@Log4j2
@Component
public class TransferAgentProcessHandler {

    @Resource
    private MakeCallHandler makeCallHandler;
    @Resource
    private HangupCallHandler hangupCallHandler;

    public void hanlder(CallInfo callInfo, AgentInfo agentInfo, String thisDeviceId) {
        String deviceId = RandomUtil.randomString(16);
        String caller = agentInfo.getCalled();
        //坐席没有sip或者分机不存在
        if (StringUtils.isBlank(caller)) {
            log.error("agent:{} sip is error, callId:{}", agentInfo.getAgentKey(), callInfo.getCallId());
            return;
        }

        log.info("callId:{} find agent:{}", callInfo.getCallId(), agentInfo.getAgentKey());
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setCaller(agentInfo.getAgentId());
        deviceInfo.setDisplay(callInfo.getCaller());
        deviceInfo.setCalled(caller);
        deviceInfo.setCallTime(new Date());
        deviceInfo.setCallId(callInfo.getCallId());
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setDeviceType(1);
        deviceInfo.setAgentKey(agentInfo.getAgentKey());
        callInfo.getDeviceList().add(deviceId);
        callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
        callInfo.setAgentKey(agentInfo.getAgentKey());
        callInfo.setCalled(agentInfo.getCalled());
        callInfo.getNextCommands().add(new NextCommand(deviceId, NextTypeEnum.NEXT_CALL_BRIDGE, thisDeviceId));
        RedisService.getCallInfoManager().put(callInfo);
        RedisService.getDeviceInfoManager().putDeviceCallId(deviceId, callInfo.getCallId());

        RouteGateWayInfo routeGateWayInfo = RedisService.getCompanyInfoManager().getRouteGateWayInfo(callInfo.getCompanyId(), caller);
        if (routeGateWayInfo == null) {
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(thisDeviceId).build());
            return;
        }
        agentInfo.setCallId(callInfo.getCallId());
        agentInfo.setDeviceId(deviceId);
        makeCallHandler.handler(MakeCallModel.builder()
                .deviceId(deviceId)
                .callId(callInfo.getCallId())
                .display(callInfo.getCaller())
                .called(caller)
                .originateTimeout(null)
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
}
