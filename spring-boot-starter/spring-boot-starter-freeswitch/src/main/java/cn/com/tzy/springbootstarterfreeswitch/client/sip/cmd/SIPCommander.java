package cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.enums.sip.VideoStreamType;
import cn.com.tzy.springbootstarterfreeswitch.exception.SsrcTransactionNotFoundException;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;

/**
 * @description:设备能力接口
 */
public interface SIPCommander {

    /**
     * 视频流停止
     */
    void streamByeCmd(SipServer sipServer, AgentVoInfo deviceVo, String stream, String callId, VideoStreamType type, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException, SsrcTransactionNotFoundException;

    public void sendAckMessage(SipServer sipServer, SessionDescription sdp, ResponseEventExt event, SIPResponse response, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent) throws InvalidArgumentException, SipException, ParseException, SdpParseException;
}
