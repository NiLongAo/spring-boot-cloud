package cn.com.tzy.springbootstarterfreeswitch.service.sip;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.LoginTypeEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.bean.ConfigModel;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.FsService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.EventResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.result.RestResultEvent;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SendRtpManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SipTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.subscribe.sip.message.SipSubscribeEvent;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.SipServer;
import cn.com.tzy.springbootstarterfreeswitch.client.sip.cmd.SIPCommanderForPlatform;
import cn.com.tzy.springbootstarterfreeswitch.vo.media.MediaRestResult;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.*;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.List;

@Log4j2
public abstract class ParentPlatformService {

    @Resource
    private SipServer sipServer;
    @Resource
    private DynamicTask dynamicTask;
    @Resource
    private SIPCommanderForPlatform sipCommanderForPlatform;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public abstract ConfigModel random();

    /**
     * 向上级平台注册
     */
    public void login(AgentVoInfo agentVoInfo,SipSubscribeEvent okEvent,SipSubscribeEvent errorEvent){
        RedisService.getRegisterServerManager().putPlatform(agentVoInfo.getAgentKey(), agentVoInfo.getKeepTimeout()+ SipConstant.DELAY_TIME, Address.builder().agentKey(agentVoInfo.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
        RedisService.getSipTransactionManager().delParentPlatform(agentVoInfo.getAgentKey());
        register(agentVoInfo,ok->{
            if(okEvent != null){
                okEvent.response(ok);
            }
        }, error->{
            log.info("[国标级联] {}, 发起注册，失败", agentVoInfo.getAgentKey());
            if(errorEvent!=null){
                errorEvent.response(error);
            }
        });
    }


    private void register(AgentVoInfo agentVoInfo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        SipTransactionInfo sipTransactionInfo = RedisService.getSipTransactionManager().findParentPlatform(agentVoInfo.getAgentKey());
        if(sipTransactionInfo == null){
            sipTransactionInfo = new SipTransactionInfo();
            RedisService.getSipTransactionManager().putParentPlatform(agentVoInfo.getAgentKey(),sipTransactionInfo);
        }
        //如果注册三次后还未注册成功则取消注册
        if(sipTransactionInfo.getRegisterAliveReply() > 3){
            sipTransactionInfo.setRegisterAliveReply(0);
            sipTransactionInfo.setKeepAliveReply(0);
            RedisService.getSipTransactionManager().putParentPlatform(agentVoInfo.getAgentKey(),sipTransactionInfo);
            errorEvent.response(new EventResult<RestResultEvent>(new RestResultEvent(RespCode.CODE_2.getValue(),String.format("[国标级联]：%s, 平台注册三次尚未成功则放弃", agentVoInfo.getAgentKey()))));
            offline(agentVoInfo);
            return;
        }
        sipTransactionInfo.setKeepAliveReply(0);
        sipTransactionInfo.setRegisterAliveReply(sipTransactionInfo.getRegisterAliveReply() +1);
        RedisService.getSipTransactionManager().putParentPlatform(agentVoInfo.getAgentKey(),sipTransactionInfo);
        agentVoInfo.setLoginType(LoginTypeEnum.SOCKET.getType());
        try {
            sipCommanderForPlatform.register(sipServer, agentVoInfo,null, true, ok->{
                if(okEvent !=null){
                    okEvent.response(ok);
                }
            }, error->{
                offline(agentVoInfo);
                if(errorEvent !=null){
                    errorEvent.response(error);
                }
            });
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[命令发送失败] 国标级联注册: {}", e.getMessage());
        }
    }

    /**
     * 向上级平台注销
     */
    public void unregister(AgentVoInfo parentPlatformVo, SipSubscribeEvent okEvent, SipSubscribeEvent errorEvent){
        parentPlatformVo.setLoginType(LoginTypeEnum.SOCKET.getType());
        try {
            sipCommanderForPlatform.unregister(sipServer, parentPlatformVo, (ok->{
                if(okEvent != null){
                    okEvent.response(ok);
                }
            }), (eventResult)->{
                log.info("[国标级联] {}, 发起注销，失败", parentPlatformVo.getAgentKey());
                if(errorEvent != null){
                    errorEvent.response(eventResult);
                }
            });
        } catch (InvalidArgumentException | ParseException | SipException e) {
            log.error("[命令发送失败] 国标级联注销: {}", e.getMessage());
            offline(parentPlatformVo);
        }
    }

    public void online(AgentVoInfo agentVoInfo, SipTransactionInfo sipTransactionInfo){
        log.info("[国标级联]：{}, 平台上线", agentVoInfo.getAgentKey());
        if(sipTransactionInfo != null){
            SipTransactionManager sipTransactionManager = RedisService.getSipTransactionManager();
            sipTransactionManager.putParentPlatform(agentVoInfo.getAgentKey(),sipTransactionInfo);
        }
        //设置上线
        FsService.getAgentService().online(agentVoInfo, null);
        //添加注册任务
        this.registerTask(agentVoInfo,false);
        RedisService.getRegisterServerManager().putPlatform(agentVoInfo.getAgentKey(), agentVoInfo.getKeepTimeout()+SipConstant.DELAY_TIME, Address.builder().agentKey(agentVoInfo.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
        RedisService.getAgentNotifySubscribeManager().addPresenceSubscribe(agentVoInfo);
    }

    public void offline(AgentVoInfo agentVoInfo){
        log.info("[平台离线]：{}", agentVoInfo.getAgentKey());
        RedisService.getAgentNotifySubscribeManager().removePresenceSubscribe(agentVoInfo);
        RedisService.getRegisterServerManager().delPlatform(agentVoInfo.getAgentKey());
        // 停止所有推流
        log.info("[平台离线] {}, 停止所有推流", agentVoInfo.getAgentKey());
        stopAllPush(agentVoInfo.getAgentKey());
        //清除注册定时
        this.registerTask(agentVoInfo,true);
        // 停止目录订阅回复
        log.info("[平台离线] {}, 停止订阅回复", agentVoInfo.getAgentKey());
        FsService.getAgentService().offline(agentVoInfo.getAgentKey());
    }

    /**
     * 上级平台注册任务（到注册时间后重新注册）
     */
    private void registerTask(AgentVoInfo agentVoInfo, boolean isClose){
        // 设置超时重发， 后续从底层支持消息重发
        String key = SipConstant.PLATFORM_REGISTER_TASK_CATCH_PREFIX + agentVoInfo.getAgentKey();
        AgentVoInfo agentVo = RedisService.getAgentInfoManager().get(agentVoInfo.getAgentKey());
        if(agentVo == null){
            log.error(String.format("[国标级联] 平台：{}注册即将到期，开始续订失败 agentKey : %s,未获取客服缓存信息",agentVoInfo.getAgentKey()));
            dynamicTask.stop(key);
            return;
        }

        if(isClose){
            dynamicTask.stop(key);
            return;
        }
        if (dynamicTask.isAlive(key)) {
            return;
        }
        int expires= Math.max(agentVo.getExpires(),20)-SipConstant.DELAY_TIME;
        dynamicTask.startCron(key, expires, expires,()->{
            log.info("[国标级联] 平台：{}注册即将到期，开始续订", agentVo.getAgentKey());
            register(agentVo, null,error -> {
                log.error(String.format("[国标级联] 平台：{}注册即将到期，开始续订失败 code : %s,msg : %s",error.getStatusCode(),error.getMsg()));
                dynamicTask.stop(key);
            });
        });
    }

    private void stopAllPush(String agentCode) {
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        List<SendRtp> sendRtpItems = sendRtpManager.querySendRTPServerByChnnelId(agentCode);
        if (sendRtpItems != null && !sendRtpItems.isEmpty()) {
            for (SendRtp sendRtpItem : sendRtpItems) {
                if(sendRtpItem.getAudioInfo()!=null){
                    stopPush(sendRtpItem.getMediaServerId(),sendRtpItem.getAudioInfo());
                }
                if(sendRtpItem.getVideoInfo()!=null){
                    stopPush(sendRtpItem.getMediaServerId(),sendRtpItem.getVideoInfo());
                }
                sendRtpManager.deleteSendRTPServer(null, sendRtpItem.getPushStreamId(), null);
            }
        }
    }

    private void stopPush(String mediaServerId,SendRtp.SendRtpInfo sendRtpItem){
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        ssrcConfigManager.releaseSsrc(mediaServerId,sendRtpItem.getSsrc());
        MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(mediaServerId);
        if (mediaServerVo == null) {
            log.error("[停止RTP推流] 失败: 流媒体[{}]未上线",mediaServerId);
            return;
        }
        MediaRestResult mediaRestResult = MediaClient.stopSendRtp(mediaServerVo, "__defaultVhost__", sendRtpItem.getApp(), sendRtpItem.getStreamId(), sendRtpItem.getSsrc());
        if (mediaRestResult == null) {
            log.error("[停止RTP推流] 失败: 请检查ZLM服务");
        } else if (mediaRestResult.getCode() == 0) {
            log.info("[停止RTP推流] 成功");
        } else {
            log.error("[停止RTP推流] 失败: {}, 参数：{}->\r\n{}",mediaRestResult.getMsg(), JSONUtil.toJsonStr(sendRtpItem), JSONUtil.toJsonStr(mediaRestResult));
        }
    }
}
