package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.response;


import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;

import javax.annotation.Resource;

public abstract class AbstractSipResponseEvent implements SipResponseEvent{

    @Resource
    protected SipServer sipServer;
    @Resource
    protected SIPCommander sipCommander;
    @Resource
    protected SIPCommanderForPlatform sipCommanderForPlatform;

}
