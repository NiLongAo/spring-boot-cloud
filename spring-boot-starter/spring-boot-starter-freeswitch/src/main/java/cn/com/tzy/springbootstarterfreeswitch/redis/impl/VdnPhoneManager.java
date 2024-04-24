package cn.com.tzy.springbootstarterfreeswitch.redis.impl;

import cn.com.tzy.springbootstarterfreeswitch.common.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.VdnPhone;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VdnPhoneManager {

    private String FS_VDN_PHONE_INFO = RedisConstant.FS_VDN_PHONE_INFO;


    public void put(VdnPhone model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getPhone()),model,-1L);
    }


    public VdnPhone get(String phone) {
        List<String> scan = RedisUtils.keys(getKey(phone));
        if (!scan.isEmpty()) {
            return (VdnPhone)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }


    public void del(String phone){
        RedisUtils.del(getKey(phone));
    }

    private String getKey(String streamId){
        return String.format("%s%s",FS_VDN_PHONE_INFO,streamId);
    }
}
