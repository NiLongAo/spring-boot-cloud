package cn.com.tzy.springbootstarterfreeswitch.service;

import cn.com.tzy.springbootstarterfreeswitch.enums.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;

/**
 * 发送坐席消息
 */
public interface SendAgentMessage {
    void sendMessage(AgentStateEnum agentStateEnum, AgentInfo agentInfo, CallMessage callMessage);
}
