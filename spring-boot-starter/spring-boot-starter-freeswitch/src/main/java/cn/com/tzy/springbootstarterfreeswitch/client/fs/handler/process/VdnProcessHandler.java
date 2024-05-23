package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process;

import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.HangupCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.PlayBackCallHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.message.ReceiveDtmfHandler;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.NextTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallDetail;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.NextCommand;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.model.message.HangupCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.PlayBackCallModel;
import cn.com.tzy.springbootstarterfreeswitch.model.message.ReceiveDtmfModel;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 电话进vdn流程
 */
@Log4j2
@Component
public class VdnProcessHandler {
    @Resource
    private HangupCallHandler hangupCallHandler;
    @Resource
    private PlayBackCallHandler playBackCallHandler;
    @Resource
    private ReceiveDtmfHandler receiveDtmfHandler;
    @Resource
    private GroupProcessHandler groupProcessHandler;
    @Resource
    private TransferIvrProcessHandler transferIvrProcessHandler;
    @Resource
    private TransferAgentProcessHandler transferAgentProcessHandler;
    @Resource
    private TransferCallProcessHandler transferCallProcessHandler;

    public void hanlder(CallInfo callInfo, DeviceInfo deviceInfo, Long vdnId) {
        String deviceId = deviceInfo.getDeviceId();
        VdnCodeInfo vdnCodeInfo = RedisService.getCompanyInfoManager().getVdnCodeInfo(callInfo.getCompanyId(), vdnId);
        if (vdnCodeInfo == null || vdnCodeInfo.getStatus() == 0) {
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
            return;
        }
        //查询有效日程
        VdnConfigInfo vdnConfigInfo = vdnCodeInfo.getEffectiveSchedule();
        if (vdnConfigInfo == null) {
            log.warn("callId:{} not match schedule, vdn:{}", callInfo.getCallId(), vdnCodeInfo.getName());
            hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
            return;
        }
        //电话经过vdn
        CallDetail callDetail = new CallDetail();
        callDetail.setCallId(callInfo.getCallId());
        callDetail.setStartTime(new Date());
        callDetail.setDetailIndex(callInfo.getCallDetails().size() + 1);
        callDetail.setTransferType(1);
        callDetail.setTransferId(vdnConfigInfo.getVdnId());
        callInfo.getCallDetails().add(callDetail);
        log.info("vdnSchedule:{} routeType:{} routeValue:{}", vdnConfigInfo.getName(), vdnConfigInfo.getRouteType(), vdnConfigInfo.getRouteValue());
        /**
         * 1:技能组,2:放音,3:ivr,4:坐席,5:外呼 流程
         */
        switch (vdnConfigInfo.getRouteType()) {
            case 1:
                GroupInfo groupInfo = RedisService.getGroupInfoManager().get(vdnConfigInfo.getRouteValue());
                if (groupInfo == null) {
                    log.warn("callId:{} join group is null", callInfo.getCallId());
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                    return;
                }
                log.debug("callId:{} join groupId:{}, groupName:{}", callInfo.getCallId(), vdnConfigInfo.getRouteValue(), groupInfo.getName());
                callInfo.setGroupId(groupInfo.getId());
                groupProcessHandler.handler(callInfo, groupInfo, deviceId);
                break;
            case 2:
                /**
                 * 普通放音或者按键导航音 1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机
                 */
                PlaybackInfo playbackInfo = RedisService.getPlaybackInfoManager().get(vdnConfigInfo.getRouteValue());
                if (playbackInfo == null) {
                    log.warn("playback is null, callId:{} , device:{}", callInfo.getCallId(), deviceId);
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                    return;
                }
                //放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)
                log.info("playback tyep:{}, value:{}", vdnConfigInfo.getPlayType(), vdnConfigInfo.getPlayValue());
                if (vdnConfigInfo.getPlayType() != 1) {
                    NextCommand nextCommand = null;
                    switch (vdnConfigInfo.getPlayType()) {
                        case 2:
                            nextCommand = new NextCommand(deviceId, NextTypeEnum.NEXT_GROUP, vdnConfigInfo.getPlayValue());
                            break;
                        case 3:
                            nextCommand = new NextCommand(deviceId, NextTypeEnum.NEXT_IVR, vdnConfigInfo.getPlayValue());
                            break;
                        case 4:
                            nextCommand = new NextCommand(deviceId, NextTypeEnum.NEXT_VDN, vdnConfigInfo.getPlayValue());
                            break;
                        case 5:
                            nextCommand = new NextCommand(deviceId, NextTypeEnum.NEXT_HANGUP, vdnConfigInfo.getPlayValue());
                            break;
                        default:
                            break;
                    }
                    playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).isDown(false).playPath(playbackInfo.getPlayback()).build());
                    doNextCommand(callInfo, deviceInfo, nextCommand);
                    return;
                }
                /**
                 * 播放按键导航音
                 */
                receiveDtmfHandler.handler(ReceiveDtmfModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                break;

            case 3:
                /**
                 * 转IVR
                 */
                transferIvrProcessHandler.handler(callInfo, deviceInfo, vdnConfigInfo.getRouteValue());
                return;

            case 4:
                /**
                 * 转坐席
                 */
                AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(vdnConfigInfo.getRouteValue());
                if (agentVoInfo == null || agentVoInfo.getAgentState() != AgentStateEnum.READY) {
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceId).build());
                    return;
                }
                transferAgentProcessHandler.hanlder(callInfo, agentVoInfo, deviceId);
                return;
            case 5:
                /**
                 * 转外呼
                 */
                transferCallProcessHandler.hanlder(callInfo, vdnConfigInfo.getRouteValue(), deviceId);
                return;
            default:
                log.warn("vdnCode not match callId:{} , case:{}", callInfo.getCallId(), vdnConfigInfo.getRouteType());
                break;
        }
    }


    public void doNextCommand(CallInfo callInfo, DeviceInfo deviceInfo, NextCommand nextCommand) {
        if (nextCommand == null) {
            return;
        }
        callInfo.getNextCommands().remove(nextCommand);
        switch (nextCommand.getNextType()) {
            case NEXT_QUEUE_PLAY:
                playBackCallHandler.handler(PlayBackCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).isDown(false).playPath("queue.wav").build());
                break;
            case NEXT_QUEUE_OVERFLOW_GROUP:
                GroupInfo groupInfo = RedisService.getGroupInfoManager().get(nextCommand.getNextValue());
                if (groupInfo == null) {
                    log.warn("callId:{} join group is null", callInfo.getCallId());
                    hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
                    return;
                }
                groupProcessHandler.handler(callInfo, groupInfo, deviceInfo.getDeviceId());
                break;
            case NEXT_QUEUE_OVERFLOW_IVR:
                break;

            case NEXT_QUEUE_OVERFLOW_VDN:
                break;

            case NEXT_HANGUP:
                hangupCallHandler.handler(HangupCallModel.builder().mediaAddr(callInfo.getMediaHost()).deviceId(deviceInfo.getDeviceId()).build());
                break;

            case NEXT_VDN:
                break;

            case NEXT_GROUP:
                break;

            case NEXT_IVR:
                break;

            default:
                break;
        }
        callInfo.getDeviceInfoMap().put(deviceInfo.getDeviceId(), deviceInfo);
        RedisService.getCallInfoManager().put(callInfo);
    }
}
