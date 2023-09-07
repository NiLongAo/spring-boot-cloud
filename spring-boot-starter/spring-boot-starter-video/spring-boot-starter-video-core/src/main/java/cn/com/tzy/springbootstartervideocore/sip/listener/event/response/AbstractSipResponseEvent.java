package cn.com.tzy.springbootstartervideocore.sip.listener.event.response;

import cn.com.tzy.springbootstartervideocore.sip.SipServer;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommander;
import cn.com.tzy.springbootstartervideocore.sip.cmd.SIPCommanderForPlatform;

import javax.annotation.Resource;

public abstract class AbstractSipResponseEvent implements SipResponseEvent{

    @Resource
    protected SipServer sipServer;
    @Resource
    protected SIPCommander sipCommander;
    @Resource
    protected SIPCommanderForPlatform sipCommanderForPlatform;

}
