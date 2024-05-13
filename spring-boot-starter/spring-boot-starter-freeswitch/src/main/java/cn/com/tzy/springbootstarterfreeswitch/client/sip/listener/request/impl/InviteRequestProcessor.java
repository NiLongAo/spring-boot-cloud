package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * SIP命令类型： INVITE请求
 */
@Log4j2
@Component
public class InviteRequestProcessor  extends AbstractSipRequestEvent implements SipRequestEvent {

    @Override
    public String getMethod() {
        return Request.INVITE;
    }

    /**
     * 处理invite请求
     * @param evt 请求消息
     */
    @Override
    public void process(RequestEvent evt) {
        //  Invite Request消息实现，此消息一般为级联消息，上级给下级发送请求视频指令
        SIPRequest request = (SIPRequest)evt.getRequest();
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        String userId = SipUtils.getUserIdFromFromHeader(request);
        if (userId == null) {
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取坐席编号");
            return;
        }
        //获取是否有坐席登录
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().get(userId);
        if (agentVoInfo == null) {
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取坐席信息");
            return;
        }
        LoginTypeEnum loginType = LoginTypeEnum.getLoginType(agentVoInfo.getLoginType());
        if(loginType == null){
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取登陆方式");
            return;
        }
        switch (loginType){
            case SIP:
                inviteDeviceHandle(request,callIdHeader, agentVoInfo);
                break;
            case SOCKET:
                inviteParentPlatformHandle(request,callIdHeader, agentVoInfo);
                break;
            default:
                this.sendErrorMessage(request,Response.BAD_REQUEST,"暂无此登陆方式");
                return;
        }
    }

    /**
     * 处理设备的invite请求
     */
    private void inviteDeviceHandle(SIPRequest request,CallIdHeader callIdHeader, AgentVoInfo agentVoInfo){
        this.sendErrorMessage(request,Response.BAD_REQUEST,"暂未开发此方式");
        return;
    }

    /**
     * 处理上级平台的invite请求
     */
    private void inviteParentPlatformHandle(SIPRequest request, CallIdHeader callIdHeader, AgentVoInfo agentVoInfo){
        this.sendErrorMessage(request,Response.BAD_REQUEST,"暂未开发此方式");
        return;
    }

    private void sendErrorMessage(SIPRequest request, Integer status, String message){
        log.warn("无法从FromHeader的Address中{}，返回:{}",message,status);
        // 参数不全， 发400，请求错误
        try {
            responseAck(request, Response.BAD_REQUEST,message);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] invite BAD_REQUEST: {}", e.getMessage());
        }
    }
}
