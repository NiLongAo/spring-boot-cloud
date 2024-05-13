package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.utils.XmlUtils;
import org.w3c.dom.Element;

import javax.sip.RequestEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MessageHandlerAbstract implements MessageHandler{

    private Map<String, MessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    public abstract String getMessageType();

    @Override
    public void handForDevice(RequestEvent evt, AgentVoInfo deviceVo, Element element) {
        String cmd = XmlUtils.getText(element, "CmdType");
        MessageHandler messageHandler = messageHandlerMap.get(cmd);
        if (messageHandler != null) {
            messageHandler.handForDevice(evt, deviceVo, element);
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, AgentVoInfo parentPlatformVo, Element element) {
        String cmd = XmlUtils.getText(element, "CmdType");
        MessageHandler messageHandler = messageHandlerMap.get(cmd);
        if (messageHandler != null) {
            messageHandler.handForPlatform(evt, parentPlatformVo, element);
        }
    }

    public void setMessageHandler(String key,MessageHandler handler){
        messageHandlerMap.put(key,handler);
    }
    public MessageHandler getMessageHandler(String key){
       return messageHandlerMap.get(key);
    }
    public Map<String, MessageHandler> getMessageHandlerMap(){
        return messageHandlerMap;
    }

}
