package cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.register;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.properties.SipConfigProperties;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterredis.pool.AbstractMessageListener;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class SipRegisterHandle extends AbstractMessageListener {

    @Autowired
    private SipConfigProperties sipConfigProperties;

    public SipRegisterHandle() {
        super(SipConstant.VIDEO_SEND_SIP_REGISTER_MESSAGE);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object body = RedisUtils.redisTemplate.getValueSerializer().deserialize(message.getBody());
        AgentVoInfo vo = (AgentVoInfo) body;
        if(vo == null){
            return;
        }
        //触发服务注册
        SipService.getParentPlatformService().login(vo);
    }
}
