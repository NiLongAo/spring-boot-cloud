package cn.com.tzy.springbootfs.config.socket.common.agent;

import cn.com.tzy.springbootstartersocketio.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户拨打电话参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentInCallPhoneData extends Message {
    /**
     * 类型
     */
    private String type;
    /**
     * 被叫号码
     */
    private String caller;
}
