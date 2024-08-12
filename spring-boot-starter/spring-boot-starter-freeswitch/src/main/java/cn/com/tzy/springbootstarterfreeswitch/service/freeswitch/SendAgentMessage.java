package cn.com.tzy.springbootstarterfreeswitch.service.freeswitch;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;

/**
 * 发送坐席消息
 */
public interface SendAgentMessage {
    void sendMessage(AgentStateEnum agentStateEnum, AgentVoInfo agentVoInfo, CallMessage callMessage);

    void sendErrorMessage( AgentVoInfo agentVoInfo, String errorMessage);

    void sendMessage(String namespace,String agentCommon,String agentKey, RestResult<?> result);
}
