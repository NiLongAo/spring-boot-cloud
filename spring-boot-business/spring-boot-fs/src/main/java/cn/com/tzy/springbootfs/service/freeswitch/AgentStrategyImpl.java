package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentStrategy;
import org.springframework.stereotype.Service;

@Service
public class AgentStrategyImpl implements AgentStrategy {
    @Override
    public Long calculateLevel(AgentVoInfo agentVoInfo) {
        return null;
    }
}
