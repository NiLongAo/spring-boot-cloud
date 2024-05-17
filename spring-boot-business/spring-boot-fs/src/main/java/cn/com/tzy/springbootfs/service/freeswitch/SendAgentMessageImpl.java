package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootcomm.common.vo.RestResult;
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

    }

    @Override
    public void sendMessage(String namespace,String agentCommon,String agentCode, RestResult result) {
        String format = String.format("%s:%s", namespace, agentCode);
        SocketIONamespace socketIONamespace = socketIOServer.getNamespace(namespace);
        if(socketIONamespace != null){
            BroadcastOperations roomOperations = socketIONamespace.getRoomOperations(format);
            if(roomOperations !=null){
                roomOperations.sendEvent(agentCommon,result);
            }
        }
    }
}
