package cn.com.tzy.springbootfs.config.socket.common.agent;

import cn.com.tzy.springbootstartersocketio.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户推流参数
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentInPushPathData extends Message {
    /**
     * 拨打方式 1.音频 2.视频
     */
    private Integer type;
}
