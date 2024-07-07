package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstarterfreeswitch.common.socket.AgentCommon;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.model.notice.CallMessage;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.SendAgentMessage;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendAgentMessageImpl implements SendAgentMessage {

    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void sendMessage(AgentStateEnum agentStateEnum, AgentVoInfo agentVoInfo, CallMessage callMessage) {
        sendMessage(AgentCommon.SOCKET_AGENT,AgentCommon.AGENT_OUT_CALL_NOTIFICATION,agentVoInfo.getAgentKey(),RestResult.result(RespCode.CODE_0.getValue(),null,callMessage));
    }

    @Override
    public void sendMessage(String namespace, String agentCommon, String agentKey, RestResult<?> result) {
        String format = String.format("%s:%s", namespace, agentKey);
        SocketIONamespace socketIONamespace = socketIOServer.getNamespace(namespace);
        if(socketIONamespace != null){
            BroadcastOperations roomOperations = socketIONamespace.getRoomOperations(format);
            if(roomOperations !=null){
                roomOperations.sendEvent(agentCommon,result);
            }
        }
    }
}
