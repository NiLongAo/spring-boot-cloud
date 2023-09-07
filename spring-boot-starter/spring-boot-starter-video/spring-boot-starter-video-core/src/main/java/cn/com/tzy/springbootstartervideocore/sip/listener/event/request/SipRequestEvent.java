package cn.com.tzy.springbootstartervideocore.sip.listener.event.request;

import javax.sip.RequestEvent;

/**
 * 对SIP事件进行处理，包括request， response， timeout， ioException, transactionTerminated,dialogTerminated
 */
public interface SipRequestEvent {

    String getMethod();
	void process(RequestEvent event);

}
