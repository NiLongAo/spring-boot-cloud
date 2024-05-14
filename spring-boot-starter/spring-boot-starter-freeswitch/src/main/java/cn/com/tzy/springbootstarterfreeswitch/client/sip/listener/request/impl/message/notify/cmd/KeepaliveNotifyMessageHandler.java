package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message.notify.cmd;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipResponseEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message.MessageHandler;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message.notify.NotifyMessageHandler;
import cn.com.tzy.springbootstarterfreeswitch.enums.media.CmdType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * 状态信息(心跳)报送
 */
@Log4j2
@Component
public class KeepaliveNotifyMessageHandler extends SipResponseEvent implements MessageHandler {

    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public KeepaliveNotifyMessageHandler(NotifyMessageHandler handler){
        handler.setMessageHandler(CmdType.KEEPALIVE_NOTIFY.getValue(),this);
    }

    @Override
    public void handForDevice(RequestEvent evt, AgentVoInfo deviceVo, Element element) {
        if (deviceVo == null) {
            // 未注册的设备不做处理
            return;
        }
        // 个别平台保活不回复200OK会判定离线
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
        }

    }

    @Override
    public void handForPlatform(RequestEvent evt, AgentVoInfo parentPlatformVo, Element element) {
        // 个别平台保活不回复200OK会判定离线
        try {
            responseAck((SIPRequest) evt.getRequest(), Response.OK,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
        }
    }
}
