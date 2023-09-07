package cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.query;

import cn.com.tzy.springbootstartervideobasic.enums.MessageType;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.request.impl.message.MessageHandlerAbstract;
import lombok.extern.log4j.Log4j2;

/**
 * 命令类型： 查询指令
 * 命令类型： 设备状态, 设备目录信息, 设备信息, 文件目录检索(TODO), 报警(TODO), 设备配置(TODO), 设备预置位(TODO), 移动设备位置数据(TODO)
 */
@Log4j2
public class QueryMessageHandler extends MessageHandlerAbstract {
    @Override
    public String getMessageType() {
        return MessageType.QUERY.getValue();
    }
}
