package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentInfoManager {

    private String FS_AGENT_INFO = RedisConstant.FS_AGENT_INFO;

    public void put(AgentVoInfo model){
        if(model == null ){
            return;
        }
        RedisUtils.set(getKey(model.getAgentKey()),model,-1L);
    }


    public AgentVoInfo get(String agentKey) {
        List<String> scan = RedisUtils.keys(getKey(agentKey));
        if (!scan.isEmpty()) {
            return (AgentVoInfo)RedisUtils.get(scan.get(0));
        }else {
            return null;
        }
    }


    public void del(String agentKey){
        RedisUtils.del(getKey(agentKey));
    }

    private String getKey(String agentKey){
        return String.format("%s%s",FS_AGENT_INFO,agentKey);
    }
}
