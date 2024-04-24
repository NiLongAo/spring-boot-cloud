package cn.com.tzy.springbootstarterfreeswitch.handler.process;

import cn.com.tzy.springbootstarterfreeswitch.enums.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.handler.message.MakeCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.RouteGateWayInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.MakeCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.RouteGatewayModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

/**
 * 转外呼
 */
@Log4j2
@Component
public class TransferCallProcessHandler {

    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private MakeCallHandler makeCallHandler;

    public void hanlder(CallInfo callInfo, String called, String thisDeviceId) {
        log.info("callId:{} transfer to {}", callInfo.getCallId(), called);
        // 转外呼
        callInfo.setCalled(called);
        String deviceId = RandomUtil.randomString(16);
        RouteGateWayInfo routeGateWayInfo = RedisService.getCompanyInfoManager().getRouteGateWayInfo(callInfo.getCompanyId(), callInfo.getCalled());
        if (routeGateWayInfo == null) {
            log.warn("callId:{} origin error, called:{}", callInfo.getCallId(), callInfo.getCalled());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(thisDeviceId).build());
            return;
        }

        DeviceInfo device = DeviceInfo.builder()
                .deviceId(deviceId)
                .deviceType(3)
                .cdrType(2)
                .caller(callInfo.getCaller())
                .called(callInfo.getCalled())
                .callId(callInfo.getCallId())
                .callTime(new Date())
                .display(callInfo.getCaller())
                .build();
        callInfo.getDeviceList().add(deviceId);
        callInfo.getDeviceInfoMap().put(deviceId, device);

        CallDetail transferCall = new CallDetail();
        transferCall.setCallId(callInfo.getCallId());
        transferCall.setStartTime(new Date());
        transferCall.setDetailIndex(callInfo.getCallDetails().size() + 1);
        transferCall.setTransferType(5);
        callInfo.getCallDetails().add(transferCall);
        callInfo.getNextCommands().add(new NextCommand(thisDeviceId, NextTypeEnum.NEXT_CALL_BRIDGE, deviceId));
        makeCallHandler.handler(MakeCallModel.builder()
                .deviceId(deviceId)
                .callId(callInfo.getCallId())
                .display(callInfo.getCaller())
                .called(callInfo.getCalled())
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
