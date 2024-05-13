package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd;


import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.WWWAuthenticateHeader;
import java.text.ParseException;

public interface SIPCommanderForPlatform {

    /**
     * 向上级平台注册
     * @return
     */
    void register(SipServer sipServer, AgentVoInfo agentVoInfo, WWWAuthenticateHeader www, boolean isRegister, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException;
    /**
     * 向上级平台注销
     * @return
     */
    void unregister(SipServer sipServer, AgentVoInfo agentVoInfo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, ParseException, SipException;


    /**
     * 向上级平发送心跳信息
     * @return callId(作为接受回复的判定)
     */
    String keepalive(SipServer sipServer, AgentVoInfo agentVoInfo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException;

    /**
     * 向发起点播的上级回复bye
     * 点播时检查是否开启过
     */
    void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, SendRtp sendRtpItem, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException;
}
