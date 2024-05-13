package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.timeout.impl;

import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.timeout.SipTimeoutEvent;
import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.Dialog;
import javax.sip.TimeoutEvent;


/**
 * 相应超时处理
 */
@Log4j2
@Component
public class SipTimeoutEventImpl implements SipTimeoutEvent {

	@Resource
	private SipSubscribeHandle sipSubscribeHandle;

	@Override
	public void process(TimeoutEvent event) {
		try {
			Dialog dialog = event.getClientTransaction().getDialog();
			if(dialog != null){
				String callId =dialog.getCallId().getCallId();
				String key = String.format("%s:%s", SipSubscribeHandle.VIDEO_SIP_ERROR_EVENT_SUBSCRIBE_MANAGER, callId);
				RedisUtils.redisTemplate.convertAndSend(key, SerializationUtils.serialize(new EventResult(event)));
			}else {
				log.warn("[超时事件] ：dialog is null");
			}
			//清除订阅事件
		} catch (Exception e) {
			log.error("[超时事件失败]: ", e);
		}
	}
}
