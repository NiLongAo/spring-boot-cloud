package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.GroupInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupInfoManager {
    private String FS_GROUP_INFO = RedisConstant.FS_GROUP_INFO;


    public void put(GroupInfo groupInfo){
        if(groupInfo == null ){
            return;
        }
        RedisUtils.set(getKey(groupInfo.getId()),groupInfo,-1L);
    }


    public GroupInfo get(String id) {
        List<String> scan = RedisUtils.keys(getKey(id));
        if (!scan.isEmpty()) {
            return (GroupInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }


    public void del(String id){
        List<String> scan = RedisUtils.keys(getKey(id));
        for (String key : scan) {
            RedisUtils.del(key);
        }
    }

    public void delAll(){
        del("*");
    }

    private String getKey(String id){
        return String.format("%s%s",FS_GROUP_INFO,id);
    }
}
