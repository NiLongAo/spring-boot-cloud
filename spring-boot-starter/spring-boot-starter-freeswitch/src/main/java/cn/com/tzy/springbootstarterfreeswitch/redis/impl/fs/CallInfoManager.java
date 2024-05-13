package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class CallInfoManager {

    private String FS_CALL_INFO = RedisConstant.FS_CALL_INFO;


    public void put(CallInfo callInfo){
        if(callInfo == null ){
            return;
        }
        RedisUtils.set(getKey(callInfo.getCallId()),callInfo,-1L);
    }


    public CallInfo get(String callId) {
        List<String> scan = RedisUtils.keys(getKey(callId));
        if (!scan.isEmpty()) {
            return (CallInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }

    public CallInfo findDeviceId(String deviceId){
        String callId = RedisService.getDeviceInfoManager().getDeviceCallId(deviceId);
        if(StringUtils.isBlank(callId)){
            return null;
        }
        return get(callId);
    }


    public void del(String callId){
        RedisUtils.del(getKey(callId));
    }

    private String getKey(String streamId){
        return String.format("%s%s",FS_CALL_INFO,streamId);
    }

}
