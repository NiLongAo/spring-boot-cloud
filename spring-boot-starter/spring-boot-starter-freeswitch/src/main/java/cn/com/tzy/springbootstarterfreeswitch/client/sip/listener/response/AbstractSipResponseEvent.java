package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response;


import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;

@Log4j2
public abstract class AbstractSipResponseEvent implements SipResponseEvent{

    @Resource
    protected SipServer sipServer;
    @Resource
    protected SIPCommander sipCommander;
    @Resource
    protected SIPCommanderForPlatform sipCommanderForPlatform;

}
