package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceInfoManager {

    private String FS_CALLER_CALL_ID = RedisConstant.FS_CALLER_CALL_ID;
    private String FS_DEVICE_CALL_ID = RedisConstant.FS_DEVICE_CALL_ID;

    public void putCallerCallId(String caller, String callId){
        RedisUtils.set(getCallerCallIdKey(caller),callId,-1L);
    }
    public String getCallerCallId(String key) {
        List<String> scan = RedisUtils.keys(getCallerCallIdKey(key));
        if (!scan.isEmpty()) {
            return (String) RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }
    public void delCallerCallId(String key){
        RedisUtils.del(getCallerCallIdKey(key));
    }
    private String getCallerCallIdKey(String key){
        return String.format("%s%s", FS_CALLER_CALL_ID,key);
    }

    //**********************************************

    public void putDeviceCallId(String deviceId,String callId){
        RedisUtils.set(getKeyCallId(deviceId),callId,-1L);
    }
    public String getDeviceCallId(String key) {
        List<String> scan = RedisUtils.keys(getKeyCallId(key));
        if (!scan.isEmpty()) {
            return (String) RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }
    public void delDeviceCallId(String key){
        RedisUtils.del(getKeyCallId(key));
    }
    private String getKeyCallId(String key){
        return String.format("%s%s", FS_DEVICE_CALL_ID,key);
    }
}
