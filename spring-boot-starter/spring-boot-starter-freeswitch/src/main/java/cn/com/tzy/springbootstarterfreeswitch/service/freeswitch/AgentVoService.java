package cn.com.tzy.springbootstarterfreeswitch.service.freeswitch;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.utils.DynamicTask;
import cn.com.tzy.springbootstarterfreeswitch.client.media.client.MediaClient;
import cn.com.tzy.springbootstarterfreeswitch.common.sip.SipConstant;
import cn.com.tzy.springbootstarterfreeswitch.enums.fs.AgentStateEnum;
import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.redis.RedisService;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.InviteStreamManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SipTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcConfigManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.SsrcTransactionManager;
import cn.com.tzy.springbootstarterfreeswitch.service.SipService;
import cn.com.tzy.springbootstarterfreeswitch.service.sip.MediaServerVoService;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.Address;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.MediaServerVo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SipTransactionInfo;
import cn.com.tzy.springbootstarterfreeswitch.vo.sip.SsrcTransaction;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Log4j2
public abstract class AgentVoService {

    public abstract AgentVoInfo getAgentBySip(String sip);

    public abstract AgentVoInfo getAgentByKey(String agentKey);

    public abstract AgentVoInfo getAgentByCompanyCode(String company,String agentCode);
    public abstract AgentVoInfo findAgentId(String id);
    public abstract void save(AgentVoInfo entity);
    public abstract void updateStatus(Long id, boolean b);

    public abstract void startPlay(String agentCode, String stream);


    public abstract void stopPlay(String agentCode);

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    /**
     * 设备上线
     * @param agentVoInfo
     */
    public void online(AgentVoInfo agentVoInfo, SipTransactionInfo sipTransactionInfo) {
        DynamicTask dynamicTask = SpringUtil.getBean(DynamicTask.class);

        SipTransactionManager sipTransactionManager = RedisService.getSipTransactionManager();
        InviteStreamManager inviteStreamManager = RedisService.getInviteStreamManager();

        log.info("[设备上线] agentInfo：{}->{}", agentVoInfo.getAgentKey(), agentVoInfo.getRemoteAddress());
        if ( null  == agentVoInfo.getKeepTimeout() || 0 == agentVoInfo.getKeepTimeout()) {
            // 默认心跳间隔60
            agentVoInfo.setKeepTimeout(60);
        }
        if (sipTransactionInfo != null) {
            sipTransactionManager.putDevice(agentVoInfo.getAgentKey(),sipTransactionInfo);
        }
        agentVoInfo.setKeepaliveTime(new Date());
        AgentVoInfo agentVoGb = this.findAgentId(String.valueOf(agentVoInfo.getId()));
        //缓存设备注册服务
        if(agentVoGb == null){
            agentVoInfo.setState(ConstEnum.Flag.YES.getValue());
            agentVoInfo.setAgentOnline(ConstEnum.Flag.YES.getValue());
            agentVoInfo.setAgentState(AgentStateEnum.LOGIN);
            agentVoInfo.setRegisterTime(new Date());
            log.info("[设备上线,首次注册]: {}，查询设备信息以及通道信息", agentVoInfo.getAgentKey());
            this.save(agentVoInfo);
        }else {
            inviteStreamManager.clearInviteInfo(agentVoInfo.getAgentKey());
            if(agentVoInfo.getAgentOnline() == ConstEnum.Flag.NO.getValue()){
                log.info("[设备上线]: {}，查询设备信息以及通道信息", agentVoInfo.getAgentKey());
                agentVoInfo.setState(ConstEnum.Flag.YES.getValue());
                agentVoInfo.setAgentOnline(ConstEnum.Flag.YES.getValue());
                agentVoInfo.setAgentState(AgentStateEnum.LOGIN);
                agentVoInfo.setRegisterTime(new Date());
                this.save(agentVoInfo);
            }else {
                this.save(agentVoInfo);
            }
        }
        if(sipTransactionInfo != null){
            //设置设备过期任务
            String key = String.format("%s_%s", SipConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, agentVoInfo.getAgentKey());
            dynamicTask.startDelay(key, agentVoInfo.getKeepTimeout()+ SipConstant.DELAY_TIME,()->offline(agentVoInfo.getAgentKey()));
            RedisService.getRegisterServerManager().putDevice(agentVoInfo.getAgentKey(), agentVoInfo.getKeepTimeout()+ SipConstant.DELAY_TIME , Address.builder().agentKey(agentVoInfo.getAgentKey()).ip(nacosDiscoveryProperties.getIp()).port(nacosDiscoveryProperties.getPort()).build());
        }
        RedisService.getAgentInfoManager().put(agentVoInfo);
    }

    /**
     * 设备下线
     * @param agentKey
     */
    public void offline(String agentKey) {
        DynamicTask dynamicTask = SpringUtil.getBean(DynamicTask.class);
        MediaServerVoService mediaServerVoService = SipService.getMediaServerService();
        SsrcTransactionManager ssrcTransactionManager = RedisService.getSsrcTransactionManager();
        SsrcConfigManager ssrcConfigManager = RedisService.getSsrcConfigManager();
        log.info("[设备下线]， device：{}", agentKey);
        AgentVoInfo agentVoInfo = this.getAgentByKey(agentKey);
        if (agentVoInfo == null) {
            log.warn("[设备下线]：未获取设备信息 deviceId ：{}",agentKey);
            return;
        }
        String key = String.format("%s_%s", SipConstant.REGISTER_EXPIRE_TASK_KEY_PREFIX, agentVoInfo.getAgentKey());
        dynamicTask.stop(key);
        this.updateStatus(agentVoInfo.getId(),false);
        // 离线释放所有ssrc
        List<SsrcTransaction> ssrcTransactions = ssrcTransactionManager.getParamAll(agentKey, null, null, null);
        if (ssrcTransactions != null && ssrcTransactions.size() > 0) {
            for (SsrcTransaction ssrcTransaction : ssrcTransactions) {
                MediaServerVo mediaServerVo = mediaServerVoService.findOnLineMediaServerId(ssrcTransaction.getMediaServerId());
                if(mediaServerVo != null){
                    ssrcConfigManager.releaseSsrc(ssrcTransaction.getMediaServerId(), ssrcTransaction.getSsrc());
                    MediaClient.closeRtpServer(mediaServerVo, ssrcTransaction.getStream());
                    ssrcTransactionManager.remove(agentKey, ssrcTransaction.getStream());
                }
            }
        }
        RedisService.getRegisterServerManager().delDevice(agentKey);
        RedisService.getAgentInfoManager().del(agentKey);
    }

}
