package cn.com.tzy.springbootstarterfreeswitch.model.notice;

import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.CallTypeEunm;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.DirectionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallMessage implements Serializable {

    private String callId;

    private DirectionEnum direction;

    private CallTypeEunm callType;

    private String caller;

    private String called;

    private AgentStateEnum agentState;

    private String groupId;
}
