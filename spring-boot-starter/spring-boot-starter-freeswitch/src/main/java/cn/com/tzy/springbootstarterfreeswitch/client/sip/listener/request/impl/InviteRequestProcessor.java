package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SipSendMessage;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.DeviceRawContent;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Resource
    private DynamicTask dynamicTask;
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
        String userId = SipUtils.getUserIdFromHeader(request);
        if (userId == null) {
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取坐席编号");
            return;
        }
        //获取是否有坐席登录
        AgentVoInfo agentVoInfo = RedisService.getAgentInfoManager().getSip(userId);
        if (agentVoInfo == null) {
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取坐席信息");
            return;
        }else if(agentVoInfo.getAgentState() != AgentStateEnum.READY){
            try {
                responseAck(request, Response.TEMPORARILY_UNAVAILABLE,String.format("客服%s中，请稍后再拨",agentVoInfo.getAgentState().getName()));
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
            return;
        }
        LoginTypeEnum loginType = LoginTypeEnum.getLoginType(agentVoInfo.getLoginType());
        if(loginType == null){
            this.sendErrorMessage(request,Response.BAD_REQUEST,"未获取登陆方式");
            return;
        }
        try {
            responseAck(request, Response.TRYING,null);
        } catch (SipException | InvalidArgumentException | ParseException e) {
            log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
        }
        //再此处理用户是否接听逻辑
        //15秒未接听则超时，挂断电话
        String timeoutKey = String.format("%s_%s", "INVITE_REQUEST", callIdHeader.getCallId());

        dynamicTask.startDelay(timeoutKey,15,()->{
            log.warn("执行invite超时任务，断开电话以及向客服发送挂断电话 回复对方480");
            try {
                responseAck(request, Response.TEMPORARILY_UNAVAILABLE,"接听电话超时，电话挂断");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
        });
        //来电通知已在 fs事件中发送 只需要知道用户是接听操作 还是 挂断操作
        SipSendMessage.handleAgentEvent(sipServer, callIdHeader.getCallId(), ok->{
            dynamicTask.stop(timeoutKey);//关闭超时事件
            //确定接听后处理以下逻辑
            try {
                responseAck(request, Response.RINGING,null);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite RINGING: {}", e.getMessage());
            }
            DeviceRawContent deviceRawContent =SipUtils.handleDeviceRawContent(request);
            if(deviceRawContent == null){
                log.error("[视频语音流] agentKey : {} 未解析出 DeviceRawContent",agentVoInfo.getAgentKey());
                return;
            }
            if(deviceRawContent.getAudioInfo() != null){
                inviteHandle(8,deviceRawContent.getAudioInfo(),request,callIdHeader,agentVoInfo);
            }
            if(deviceRawContent.getVideoInfo() != null){
                inviteHandle(96,deviceRawContent.getVideoInfo(),request,callIdHeader,agentVoInfo);
            }
        },error->{
            dynamicTask.stop(timeoutKey);//关闭超时事件
            try {
                responseAck(request, Response.TEMPORARILY_UNAVAILABLE,"客服挂断电话");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite TRYING: {}", e.getMessage());
            }
        });

    }
    private void inviteHandle(int pt,DeviceRawContent.DeviceInfo deviceRawContent,SIPRequest request,CallIdHeader callIdHeader, AgentVoInfo agentVoInfo){
        String typeName =(pt == 8 ? "音频" : "视频");
        String streamTypeStr = null;
        if (deviceRawContent.isMediaTransmissionTCP()) {
            if (deviceRawContent.isTcpActive()) {
                streamTypeStr = "TCP-ACTIVE";
            }else {
                streamTypeStr = "TCP-PASSIVE";
            }
        }else {
            streamTypeStr = "UDP";
        }
        log.info("[{}流]设备：{}， 通道：{}, 地址：{}:{}，收流方式：{}, ssrc：{}",typeName, deviceRawContent.getUsername(), null, deviceRawContent.getAddressStr(), deviceRawContent.getPort(),streamTypeStr, deviceRawContent.getSsrc());
        MediaServerVo mediaServerVo = SipService.getMediaServerService().findMediaServerForMinimumLoad(agentVoInfo);

        //1.发送用户的视频流以及音频流

        //2.接收发送过来的 视频流 音频流
        //3.发送sdp
        //String streamId = paramOne.getStream();


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
