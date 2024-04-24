package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.enums.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;
import cn.com.tzy.springbootstarterfreeswitch.service.SendAgentMessage;
import org.springframework.stereotype.Service;

@Service
public class SendAgentMessageImpl implements SendAgentMessage {
    @Override
    public void sendMessage(AgentStateEnum agentStateEnum, AgentInfo agentInfo, CallMessage callMessage) {

    }
}
