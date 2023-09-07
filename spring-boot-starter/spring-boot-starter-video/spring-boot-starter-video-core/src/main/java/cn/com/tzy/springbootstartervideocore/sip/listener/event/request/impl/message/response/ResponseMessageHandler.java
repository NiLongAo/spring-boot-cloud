package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.response;

import cn.com.tzy.springbootstartervideobasic.enums.MessageType;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandlerAbstract;
import lombok.extern.log4j.Log4j2;

/**
 * 命令类型： 请求动作的应答
 * 命令类型： 设备控制, 报警通知, 设备目录信息查询, 目录信息查询, 目录收到, 设备信息查询, 设备状态信息查询 ......
 */
@Log4j2
public class ResponseMessageHandler extends MessageHandlerAbstract {

    @Override
    public String getMessageType() {
        return MessageType.RESPONSE.getValue();
    }
}
