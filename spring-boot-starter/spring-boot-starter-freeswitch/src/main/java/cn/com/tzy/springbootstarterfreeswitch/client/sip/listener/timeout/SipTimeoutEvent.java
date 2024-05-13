package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.timeout;

import javax.sip.TimeoutEvent;

/**
 * 相应超时处理
 */
public interface SipTimeoutEvent {

	void process(TimeoutEvent event);

}
