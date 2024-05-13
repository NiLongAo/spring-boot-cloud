package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import org.w3c.dom.Element;

import javax.sip.RequestEvent;

/**
 * 不同消息类型接口
 */
public interface MessageHandler {
    /**
     * 处理来自设备的信息
     * @param evt
     * @param deviceVo
     */
    void handForDevice(RequestEvent evt, AgentVoInfo deviceVo, Element element);

    /**
     * 处理来自平台的信息
     * @param evt
     * @param parentPlatformVo
     */
    void handForPlatform(RequestEvent evt, AgentVoInfo parentPlatformVo, Element element);
}
