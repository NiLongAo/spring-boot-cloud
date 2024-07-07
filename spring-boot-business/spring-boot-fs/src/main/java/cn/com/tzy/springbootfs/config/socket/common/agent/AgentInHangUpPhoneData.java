package cn.com.tzy.springbootfs.config.socket.common.agent;

import cn.com.tzy.springbootstartersocketio.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentInHangUpPhoneData extends Message {

    private String agentKey;//暂时无用

}
