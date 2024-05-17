package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl.message;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs.AgentInfoManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SsrcTransaction;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 消息类型处理器
 */
@Log4j2
@Component
public class MessageRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {

    @Resource
    private SipSubscribeHandle sipSubscribeHandle;

    public Map<String, MessageHandler> messageHandlerMap;

    public MessageRequestProcessor(ObjectProvider<MessageHandlerAbstract> messageHandler){
        if(!messageHandler.stream().findAny().isPresent()){
            throw new IllegalStateException(" [SipConfig error] : MessageHandler is null");
        }
        this.messageHandlerMap = messageHandler.stream().collect(Collectors.toMap(MessageHandlerAbstract::getMessageType, o -> o, (u, v) -> {
            throw new IllegalStateException(String.format("[SipConfig error] :Duplicate key %s", u));
        }, ConcurrentHashMap::new));
    }

    @Override
    public String getMethod() {
        return Request.MESSAGE;
    }

    @Override
    public void process(RequestEvent evt) {
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        AgentInfoManager agentInfoManager = RedisService.getAgentInfoManager();

        SIPRequest sipRequest = (SIPRequest)evt.getRequest();
        String agentCode = SipUtils.getUserIdFromHeader(evt.getRequest());
        CallIdHeader callIdHeader = sipRequest.getCallIdHeader();
        // 先从会话内查找
        SsrcTransaction ssrcTransaction = ssrcTransactionManager.getParamOne(null, callIdHeader.getCallId(), null,null);
        // 兼容海康 媒体通知 消息from字段不是设备ID的问题
        if (ssrcTransaction != null) {
            agentCode = ssrcTransaction.getAgentCode();
        }
        SIPRequest request = (SIPRequest) evt.getRequest();
        // 查询设备是否存在
        AgentVoInfo agentVoInfo = agentInfoManager.get(agentCode);
        if(agentVoInfo != null){
            String hostAddress = request.getRemoteAddress().getHostAddress();
            int remotePort = request.getRemotePort();
            //不相等判断未注册
            if ( StringUtils.isNotBlank(agentVoInfo.getRemoteAddress()) &&!agentVoInfo.getRemoteAddress().equals(hostAddress + ":" + remotePort)) {
                agentVoInfo = null;
            }
        }
        try {
            if(agentVoInfo == null ){
                // 不存在则回复404
                responseAck(request, Response.NOT_FOUND, "device "+ agentCode +" not found");
                log.warn("[设备未找到 ]deviceId: {}, callId: {}", agentCode, callIdHeader.getCallId());
                String key = String.format("%s:%s", SipSubscribeHandle.VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER, callIdHeader.getCallId());
                RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new EventResult(new RestResultEvent(RespCode.CODE_2.getValue(),"[ 设备未找到 ]agentCode: {}",agentCode))));
                return;
            }
            Element rootElement = getRootElement(evt);
            if (rootElement == null) {
                log.error("处理MESSAGE请求  未获取到消息体{}", evt.getRequest());
                responseAck(request, Response.BAD_REQUEST, "content is null");
                return;
            }
            String name = rootElement.getNodeName();
            MessageHandler messageHandler = messageHandlerMap.get(name);
            if (messageHandler != null) {
                if (StringUtils.isBlank(agentVoInfo.getRemoteAddress())) {
                    messageHandler.handForDevice(evt, agentVoInfo, rootElement);
                }else { // 由于上面已经判断都为null则直接返回，所以这里device和parentPlatform必有一个不为null
                    messageHandler.handForPlatform(evt, agentVoInfo, rootElement);
                }
            }else {
                // 不支持的message
                // 不存在则回复415
                responseAck(request, Response.UNSUPPORTED_MEDIA_TYPE, "Unsupported message type, must Control/Notify/Query/Response");
            }
        } catch (SipException e) {
            log.error("SIP 回复错误", e);
        } catch (InvalidArgumentException e) {
            log.error("参数无效", e);
        } catch (ParseException e) {
            log.error("SIP回复时解析异常", e);
        }catch (Exception e){
            log.error("消息处理异常:",e);
        }
    }
}
