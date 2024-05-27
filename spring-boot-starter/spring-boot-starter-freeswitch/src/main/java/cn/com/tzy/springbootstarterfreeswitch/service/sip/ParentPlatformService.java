package cn.com.tzy.springbootstarterfreeswitch.service.sip;

import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
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
        //添加保活任务
        this.keepaliveTask(agentVoInfo,false);
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
        //agentInfo
        this.keepaliveTask(agentVoInfo,true);
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
        if(isClose){
            dynamicTask.stop(key);
            return;
        }
        if (dynamicTask.isAlive(key)) {
            return;
        }
        int expires= Math.max(agentVoInfo.getExpires(),20)-SipConstant.DELAY_TIME;
        dynamicTask.startCron(key, expires, expires,()->{
            log.info("[国标级联] 平台：{}注册即将到期，开始续订", agentVoInfo.getAgentKey());
            register(agentVoInfo, null,error -> {
                log.error(String.format("[国标级联] 平台：{}注册即将到期，开始续订失败 code : %s,msg : %s",error.getStatusCode(),error.getMsg()));
                dynamicTask.stop(key);
            });
        });
    }

    /**
     * 上级平台保活任务（到保活时间后进行保活）
     */
    private void keepaliveTask(AgentVoInfo agentVoInfo, boolean isClose){
        final String keepaliveTaskKey = SipConstant.PLATFORM_KEEPALIVE_PREFIX + agentVoInfo.getAgentKey();
        if(isClose){
            dynamicTask.stop(keepaliveTaskKey);
            return;
        }
        if (dynamicTask.isAlive(keepaliveTaskKey)) {
            return;
        }
        log.info("[国标级联]：{}, 定时上级平台心跳保活任务", agentVoInfo.getAgentKey());
        dynamicTask.startCron(keepaliveTaskKey,1, agentVoInfo.getKeepTimeout(),()->{
            try {
                SipTransactionInfo parentPlatform = RedisService.getSipTransactionManager().findParentPlatform(agentVoInfo.getAgentKey());
                if(parentPlatform == null){
                    log.error("[国标级联]：{},心跳时未发现,上级平台注册信息",agentVoInfo.getAgentKey());
                    return;
                }else if(parentPlatform.getKeepAliveReply() > 3){//心跳发送三次后如果还未回应则 平台下线
                    log.error("[国标级联]：{},心跳发送三次后如 平台还未回应,平台下线",agentVoInfo.getAgentKey());
                    offline(agentVoInfo);
                    return;
                }
                parentPlatform.setKeepAliveReply(parentPlatform.getKeepAliveReply()+1);
                RedisService.getSipTransactionManager().putParentPlatform(agentVoInfo.getAgentKey(),parentPlatform);
                sipCommanderForPlatform.keepalive(sipServer, agentVoInfo, ok->{
                    log.info("[国标级联]：{}, 定时上级平台心跳保活任务成功", agentVoInfo.getAgentKey());
                    if(parentPlatform.getKeepAliveReply() > 1){
                        parentPlatform.setKeepAliveReply(0);
                        RedisService.getSipTransactionManager().putParentPlatform(agentVoInfo.getAgentKey(),parentPlatform);
                    }
                    RedisService.getRegisterServerManager().putPlatform(agentVoInfo.getAgentKey(),agentVoInfo.getKeepTimeout()+SipConstant.DELAY_TIME, Address.builder().agentKey(agentVoInfo.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
                },error->{
                    register(agentVoInfo,null, errorEvent -> {
                        log.info("[国标级联] {}，心跳超时后再次发起注册仍然失败，开始定时发起注册，间隔为1分钟", agentVoInfo.getAgentKey());
                        dynamicTask.stop(keepaliveTaskKey);
                    });
                });

            }catch (SipException | InvalidArgumentException | ParseException e){
                log.error("[命令发送失败] 国标级联 发送心跳: {}", e.getMessage());
            }
        });
    }

    private void stopAllPush(String agentCode) {
        SendRtpManager sendRtpManager = RedisService.getSendRtpManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        List<SendRtp> sendRtpItems = sendRtpManager.querySendRTPServerByChnnelId(agentCode);
        if (sendRtpItems != null && !sendRtpItems.isEmpty()) {
            for (SendRtp sendRtpItem : sendRtpItems) {
                ssrcConfigManager.releaseSsrc(sendRtpItem.getMediaServerId(),sendRtpItem.getSsrc());
                sendRtpManager.deleteSendRTPServer(sendRtpItem.getAgentKey(), null, null);
                MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(sendRtpItem.getMediaServerId());
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
    }

}
