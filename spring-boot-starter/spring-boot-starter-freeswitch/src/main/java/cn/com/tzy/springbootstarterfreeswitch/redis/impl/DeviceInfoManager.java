package cn.com.tzy.springbootstarterfreeswitch.redis.impl;

import cn.com.tzy.springbootstarterfreeswitch.common.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.call.DeviceInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceInfoManager {

    private String FS_DEVICE_INFO = RedisConstant.FS_DEVICE_INFO;
    private String FS_DEVICE_CALLID = RedisConstant.FS_DEVICE_CALLID;

    public void putDeviceInfo(DeviceInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKeyDeviceInfo(model.getDeviceId()),model,-1L);
    }
    public DeviceInfo getDeviceInfo(String key) {
        List<String> scan = RedisUtils.keys(getKeyDeviceInfo(key));
        if (!scan.isEmpty()) {
            return (DeviceInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }
    public void delDeviceInfo(String key){
        RedisUtils.del(getKeyDeviceInfo(key));
    }
    private String getKeyDeviceInfo(String key){
        return String.format("%s%s",FS_DEVICE_INFO,key);
    }

    //**********************************************

    public void putDeviceCallId(String deviceId,String callId){
        RedisUtils.set(getKeyDeviceInfo(deviceId),callId,-1L);
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
        RedisUtils.del(getKeyDeviceInfo(key));
    }
    private String getKeyCallId(String key){
        return String.format("%s%s",FS_DEVICE_CALLID,key);
    }
}
