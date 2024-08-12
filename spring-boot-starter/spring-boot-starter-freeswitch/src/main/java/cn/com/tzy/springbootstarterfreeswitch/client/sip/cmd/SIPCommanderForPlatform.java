package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd;


import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.media.HookEvent;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SSRCInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SendRtp;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.ProxyAuthenticateHeader;
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
    /**
     * 向发起点播的上级回复bye
     * 点播时检查是否开启过
     */
    void streamByeCmd(SipServer sipServer, AgentVoInfo agentVoInfo, String audioStream, String videoStream, String callId, VideoStreamType type, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws SipException, InvalidArgumentException, ParseException;

    /**
     * presence 订阅、取消订阅信息
     * @param deviceVo		视频设备
     * @return				true = 命令发送成功
     */
    SIPRequest presenceSubscribe(SipServer sipServer, AgentVoInfo deviceVo, SIPRequest request, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException;

    /**
     * 请求拨打电话请求
     */
    SIPRequest callPhone(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo deviceVo,String caller,String callBackId, HookEvent hookEvent, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException;
    /**
     * 重新认证发起拨打电话
     */
    SIPRequest callPhone(SipServer sipServer,AgentVoInfo agentVoInfo, ProxyAuthenticateHeader header, SIPRequest sipRequest, SIPResponse response) throws InvalidArgumentException, SipException, ParseException;
    /**
     * 发起拨打电话
     */
    public String createSdp(SipServer sipServer, MediaServerVo mediaServerVo, SSRCInfo videoSsrcInfo, SSRCInfo audioSsrcInfo, AgentVoInfo agentVoInfo);
}
