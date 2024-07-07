package cn.com.tzy.springbootfs.config.socket.common.agent;

import cn.com.tzy.springbootstartersocketio.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 更新用户状态或 获取客服最新信息
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentInAnswerPhoneData extends Message {
    /**
     * 用户来电操作
     * 1.挂断 2.接听
     */
    private Integer type;
    /**
     * 通话唯一标识
     */
    private String callId;
}
