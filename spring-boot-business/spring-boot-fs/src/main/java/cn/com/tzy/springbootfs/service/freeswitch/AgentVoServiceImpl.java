package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.AgentVoService;
import org.springframework.stereotype.Service;

@Service
public class AgentVoServiceImpl extends AgentVoService {
    @Override
    public AgentVoInfo getAgentBySip(String sip) {
        return null;
    }

    @Override
    public AgentVoInfo findAgentId(String id) {
        return null;
    }

    @Override
    public void save(AgentVoInfo entity) {

    }

    @Override
    public void updateStatus(Long id, boolean b) {

    }

    @Override
    public void startPlay(String agentCode, String stream) {

    }

    @Override
    public void stopPlay(String agentCode) {

    }
}
