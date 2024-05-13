package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message.notify;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message.MessageHandlerAbstract;
import cn.com.tzy.springbootstarterfreeswitch.enums.media.MessageType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * 命令类型： 通知命令， 参看 A.2.5 通知命令
 * 命令类型： 状态信息(心跳)报送, 报警通知, 媒体通知, 移动设备位置数据，语音广播通知(TODO), 设备预置位(TODO)
 * @author lin
 */
@Log4j2
@Component
public class NotifyMessageHandler extends MessageHandlerAbstract {
    @Override
    public String getMessageType() {
        return MessageType.NOTIFY.getValue();
    }
}
