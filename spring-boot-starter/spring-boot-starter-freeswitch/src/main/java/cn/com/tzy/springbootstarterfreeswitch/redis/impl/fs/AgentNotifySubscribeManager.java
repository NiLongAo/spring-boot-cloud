package cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs;

import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.fs.AgentNotifyVo;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AgentNotifySubscribeManager {

    public static final String AGENT_NOTIFY = RedisConstant.AGENT_NOTIFY;
    public static final String AGENT_NOTIFY_PRESENCE = RedisConstant.AGENT_NOTIFY_PRESENCE;

    public boolean getPresenceSubscribe(String gbId){
        String key = String.format("%s%s",AGENT_NOTIFY_PRESENCE, gbId);
        return ObjectUtils.isNotEmpty(RedisUtils.get(key));
    }

    public boolean addPresenceSubscribe(AgentVoInfo agentNotifyVo){
        if (agentNotifyVo == null) {
            return false;
        }
        log.info("[添加Presence订阅] 设备{}", agentNotifyVo.getAgentCode());
        AgentNotifyVo build = AgentNotifyVo.builder().type(AgentNotifyVo.TypeEnum.PRESENCE.getValue()).operate(AgentNotifyVo.OperateEnum.ADD.getValue()).agentCode(agentNotifyVo.getAgentCode()).build();
        RedisUtils.redisTemplate.convertAndSend(AGENT_NOTIFY,build);
        return true;
    }

    public boolean removePresenceSubscribe(AgentVoInfo agentNotifyVo) {
        if (agentNotifyVo == null) {
            return false;
        }
        log.info("[移除Presence订阅]: {}",  agentNotifyVo.getAgentCode());
        AgentNotifyVo build = AgentNotifyVo.builder().type(AgentNotifyVo.TypeEnum.PRESENCE.getValue()).operate(AgentNotifyVo.OperateEnum.DEL.getValue()).agentCode(agentNotifyVo.getAgentCode()).build();
        RedisUtils.redisTemplate.convertAndSend(AGENT_NOTIFY,build);
        return true;
    }

}
