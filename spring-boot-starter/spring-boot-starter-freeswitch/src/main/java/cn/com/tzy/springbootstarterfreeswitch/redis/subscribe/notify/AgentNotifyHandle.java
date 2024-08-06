package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.notify;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.common.fs.RedisConstant;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.vo.fs.AgentNotifyVo;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.hutool.core.codec.Base64;
import cn.hutool.extra.spring.SpringUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.ToHeader;
import java.text.ParseException;

@Log4j2
@Component
public class AgentNotifyHandle extends AbstractMessageListener {

    @Resource
    private DynamicTask dynamicTask;

    public static final String AGENT_NOTIFY = RedisConstant.AGENT_NOTIFY;
    public static final String AGENT_NOTIFY_PRESENCE = RedisConstant.AGENT_NOTIFY_PRESENCE;

    public AgentNotifyHandle() {
        super(AGENT_NOTIFY);
    }


    @Override
    public void onMessage(Message message, byte[] bytes) {
        Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
        AgentNotifyVo event = (AgentNotifyVo) body;
        if(event == null){
            log.error("[订阅消息]：消息接收异常！");
            return;
        }
        if(event.getType().equals(AgentNotifyVo.TypeEnum.PRESENCE.getValue())){
            if(event.getOperate().equals(AgentNotifyVo.OperateEnum.ADD.getValue())){
                addPresenceSubscribe(event.getAgentKey());
            }else if(event.getOperate().equals(AgentNotifyVo.OperateEnum.DEL.getValue())){
                delPresenceSubscribe(event.getAgentKey());
            }else {
                log.error("[订阅消息]：消息操作类型错误！");
            }
        }else {
            log.error("[订阅消息]：消息类型错误！");
        }
    }

    private void addPresenceSubscribe(String agentKey){
        SipServer sipServer = SpringUtil.getBean(SipServer.class);
        SIPCommanderForPlatform sipCommanderForPlatform = SpringUtil.getBean(SIPCommanderForPlatform.class);
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if(agentVoInfo == null){
            return;
        }
        String key = String.format("%s%s",AGENT_NOTIFY_PRESENCE, agentVoInfo.getAgentKey());
        if(dynamicTask.contains(key)){
            return;
        }
        dynamicTask.startCron(key, 1,590,()->{
            SIPRequest request = null;
            Object req = RedisUtils.get(key);
            if(ObjectUtils.isNotEmpty(req)){
                Object deserialize = SerializationUtils.deserialize(Base64.decode((String) req));
                request = (SIPRequest) deserialize;
            }
            SIPRequest sipRequest = null;
            try {
                sipRequest = sipCommanderForPlatform.presenceSubscribe(sipServer, agentVoInfo, request, eventResult -> {
                    ResponseEvent event = (ResponseEvent) eventResult.getEvent();
                    // 成功
                    log.info("[Presence订阅]成功： {}", agentVoInfo.getAgentKey());
                    ToHeader toHeader = (ToHeader)event.getResponse().getHeader(ToHeader.NAME);
                    Object o = RedisUtils.get(key);
                    if(ObjectUtils.isEmpty(o)){
                        return;
                    }
                    Object deserialize = SerializationUtils.deserialize(Base64.decode((String) o));
                    SIPRequest rq = (SIPRequest) deserialize;
                    try {
                        rq.getToHeader().setTag(toHeader.getTag());
                        long expire = RedisUtils.getExpire(key);
                        RedisUtils.set(key,SerializationUtils.serialize(rq),expire);
                    } catch (ParseException e) {
                        log.info("[Presence订阅]成功： 但为request设置ToTag失败");
                        RedisUtils.del(key);
                    }

                },eventResult -> {
                    RedisUtils.del(key);
                    // 失败
                    log.warn("[Presence订阅]失败，信令发送失败： {}-{} ", agentVoInfo.getAgentKey(), eventResult.getMsg());
                });
            } catch (InvalidArgumentException | SipException | ParseException e) {
                log.error("[Presence发送失败] 目录订阅: {}", e.getMessage());
            }
            if (sipRequest != null) {
                RedisUtils.set(key, SerializationUtils.serialize(sipRequest),590+ SipConstant.DELAY_TIME);
            }

        });
    }

    private void delPresenceSubscribe(String agentKey){
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(agentKey);
        if(agentVoInfo == null){
            return;
        }
        log.info("[移除目录订阅]: {}", agentVoInfo.getAgentKey());
        String key = String.format("%s%s",AGENT_NOTIFY_PRESENCE, agentVoInfo.getAgentKey());
        dynamicTask.stop(key);
        RedisUtils.del(key);
    }

}
