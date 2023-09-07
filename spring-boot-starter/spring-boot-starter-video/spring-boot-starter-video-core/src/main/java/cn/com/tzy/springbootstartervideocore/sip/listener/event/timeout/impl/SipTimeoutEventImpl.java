package cn.com.tzy.springbootstartervideocore.sip.listener.event.timeout.impl;

import cn.com.tzy.springbootstarterredis.utils.RedisUtils;
import cn.com.tzy.springbootstartervideocore.model.EventResult;
import cn.com.tzy.springbootstartervideocore.redis.subscribe.sip.message.SipSubscribeHandle;
import cn.com.tzy.springbootstartervideocore.sip.listener.event.timeout.SipTimeoutEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import javax.sip.Dialog;
import javax.sip.TimeoutEvent;


/**
 * 相应超时处理
 */
@Log4j2
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
