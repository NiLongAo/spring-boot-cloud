package cn.com.tzy.springbootstarterfreeswitch.handler.process;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Ivr 相关操作实现（暂未实现）
 */
@Log4j2
@Component
public class TransferIvrProcessHandler {


    //暂未实现
    public void handler(CallInfo callInfo, DeviceInfo deviceInfo, String ivrId) {
        RedisService.getCallInfoManager().put(callInfo);
        Map<String, Object> params = new HashMap<>();
        params.put("callId", callInfo.getCallId());
        params.put("deviceId", deviceInfo.getDeviceId());
        params.put("ivrId", ivrId);
    }
}
