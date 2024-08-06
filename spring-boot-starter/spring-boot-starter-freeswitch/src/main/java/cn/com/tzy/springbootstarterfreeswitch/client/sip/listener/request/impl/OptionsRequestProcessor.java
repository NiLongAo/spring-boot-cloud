package cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.impl;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.AbstractSipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.listener.request.SipRequestEvent;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.utils.SipUtils;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.Address;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Date;

/**
 * SIP命令类型： OPTIONS请求,FS主动发送ping命令与心态机制一致
 * 每28秒fs会发送一次心跳
 */
@Log4j2
@Component
public class OptionsRequestProcessor extends AbstractSipRequestEvent implements SipRequestEvent {
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Override
    public String getMethod() {
        return Request.OPTIONS;
    }

    @Override
    public void process(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        String agentKey = SipUtils.getUserIdToHeader(request); //可能是设备，也可能是上级平台
        AgentVoInfo agent = RedisService.getAgentInfoManager().getSip(agentKey);
        if(agent == null){
            log.warn("未获取客服：{} ，注册缓存信息",agentKey);
            try {
                responseAck(request, Response.UNAUTHORIZED,"未获取到当前客服");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
            }
            AgentVoInfo agentVoInfo = FsService.getAgentService().getAgentBySip(agentKey);
            if(agentVoInfo == null){
                log.warn("未获取客服：{} 信息",agentKey);
                return;
            }
            //缓存设备注册服务 注销时需要
            RedisService.getRegisterServerManager().putPlatform(agentVoInfo.getAgentKey(),agentVoInfo.getKeepTimeout()+ SipConstant.DELAY_TIME ,Address.builder().agentKey(agentVoInfo.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
            RedisService.getAgentInfoManager().put(agentVoInfo);
            SipService.getParentPlatformService().unregister(agentVoInfo,null,null);
            return;
        }
        if(agent.getState() == ConstEnum.Flag.NO.getValue() || agent.expire()){
            if(agent.getState() == ConstEnum.Flag.NO.getValue()){
                log.info("设备离线，重新注册");
            }else {
                log.info("设备注册过期，重新注册");
            }
            // 注册时间过期，需重新注册
            try {
                responseAck(request, Response.UNAUTHORIZED,"注册过期");
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] 心跳回复: {}", e.getMessage());
            }
            if(agent.getState() != ConstEnum.Flag.NO.getValue()){
                FsService.getAgentService().offline(agent.getAgentKey());
            }
            return;
        }
        //回复订阅已接收
        try {
            responseAck(request, Response.OK, null);
        }catch (SipException | InvalidArgumentException | ParseException e) {
            e.printStackTrace();
        }
        //缓存设备注册服务
        RedisService.getRegisterServerManager().putPlatform(agent.getAgentKey(),agent.getKeepTimeout() + SipConstant.DELAY_TIME ,Address.builder().agentKey(agent.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
        Date date = new Date();
        long between = 60L;
        if(agent.getKeepaliveTime() != null){
            between = DateUtil.between(agent.getKeepaliveTime(), date, DateUnit.SECOND);
            if (between <= 3){
                log.info("[收到心跳] 心跳发送过于频繁，已忽略 device: {}, callId: {}", agent.getAgentKey(), request.getCallIdHeader().getCallId());
                return;
            }
        }
        // 刷新过期任务,如果三次心跳失败，则设置设备离线
        dynamicTask.startDelay(String.format("%s_%s", SipConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, agent.getAgentKey()), agent.getKeepTimeout()*3,() -> FsService.getAgentService().offline(agent.getAgentKey()));
        Address remoteAddressInfo = SipUtils.getRemoteAddressFromRequest(request, videoProperties.getSipUseSourceIpAsRemoteAddress());
        if (!agent.getFsHost().equalsIgnoreCase(remoteAddressInfo.getIp()) || Integer.parseInt(agent.getFsPost()) != remoteAddressInfo.getPort()) {
            log.info("[心跳] 设备{}地址变化, 远程地址为: {}:{}", agent.getAgentKey(), remoteAddressInfo.getIp(), remoteAddressInfo.getPort());
            agent.setFsPost(String.valueOf(remoteAddressInfo.getPort()));
            agent.setMediaAddress(remoteAddressInfo.getIp().concat(":").concat(String.valueOf(remoteAddressInfo.getPort())));
            agent.setFsHost(remoteAddressInfo.getIp());
        }
        if (agent.getKeepaliveTime() == null) {
            agent.setKeepTimeout(60);
        }else {
            if (between > 10) {
                agent.setKeepTimeout((int)between);
            }
        }
        agent.setKeepaliveTime(date);
        if (agent.getState() == ConstEnum.Flag.YES.getValue()) {
            RedisService.getAgentInfoManager().put(agent);
        }
    }
}
