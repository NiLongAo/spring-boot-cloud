package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.AgentService;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceImpl implements AgentService {
    @Override
    public AgentInfo getAgentBySip(String sip) {
        return null;
    }
}
