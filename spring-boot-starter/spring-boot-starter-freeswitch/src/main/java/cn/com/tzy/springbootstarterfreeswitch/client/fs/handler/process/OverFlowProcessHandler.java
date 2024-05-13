package cn.com.tzy.springbootstarterfreeswitch.client.fs.handler.process;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupOverFlowInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 电话溢出
 */
@Log4j2
@Component
public class OverFlowProcessHandler {

    @Resource
    private GroupProcessHandler groupProcessHandler;
    @Resource
    private VdnProcessHandler vdnProcessHandler;
    @Resource
    private TransferIvrProcessHandler transferIvrProcessHandler;

    public void handler(CallInfo callInfo, String deviceId, GroupOverFlowInfo groupOverFlow) {
        log.info("callId:{} handleType is overflow, overflowType:{}, overflowValue:{}", callInfo.getCallId(), groupOverFlow.getOverflowType(), groupOverFlow.getOverflowValue());
        callInfo.setOverflowCount(callInfo.getOverflowCount() + 1);
        /**
         * 溢出(1:group,2:ivr,3:vdn)
         */
        switch (groupOverFlow.getOverflowType()) {
            case 1:
                log.info("callId:{} overflow to group:{}", callInfo.getCallId(), callInfo.getCallId());
                GroupInfo overFlowGroup = RedisService.getGroupInfoManager().get(groupOverFlow.getOverflowValue());
                groupProcessHandler.handler(callInfo, overFlowGroup, deviceId);
                break;
            case 2:
                log.info("callId:{} overflow to ivr:{}", callInfo.getCallId(), callInfo.getCallId());
                transferIvrProcessHandler.handler(callInfo, callInfo.getDeviceInfoMap().get(deviceId), groupOverFlow.getOverflowValue());
                break;

            case 3:
                log.info("callId:{} overflow to vdn:{}", callInfo.getCallId(), groupOverFlow.getOverflowValue());
                vdnProcessHandler.hanlder(callInfo, callInfo.getDeviceInfoMap().get(deviceId), Long.valueOf(groupOverFlow.getOverflowValue()));
                break;
            default:
                break;
        }
    }
}
