package cn.com.tzy.springbootstarterfreeswitch.redis;

import cn.com.tzy.springbootstarterfreeswitch.redis.impl.fs.*;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.MediaServerManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.RecordMp4Manager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.media.StreamChangedManager;
import cn.com.tzy.springbootstarterfreeswitch.redis.impl.sip.*;
import cn.hutool.extra.spring.SpringUtil;

public class RedisService {

    public static CallInfoManager getCallInfoManager(){return SpringUtil.getBean(CallInfoManager.class);}
    public static VdnPhoneManager getVdnPhoneManager(){return SpringUtil.getBean(VdnPhoneManager.class);}
    public static CompanyInfoManager getCompanyInfoManager(){return SpringUtil.getBean(CompanyInfoManager.class);}
    public static GroupInfoManager getGroupInfoManager(){return SpringUtil.getBean(GroupInfoManager.class);}
    public static AgentInfoManager getAgentInfoManager(){return SpringUtil.getBean(AgentInfoManager.class);}
    public static DeviceInfoManager getDeviceInfoManager(){return SpringUtil.getBean(DeviceInfoManager.class);}
    public static PlaybackInfoManager getPlaybackInfoManager(){return SpringUtil.getBean(PlaybackInfoManager.class);}


    //---------------Sip注册---------------------
    public static SsrcConfigManager getSsrcConfigManager(){return SpringUtil.getBean(SsrcConfigManager.class);}
    public static RegisterServerManager getRegisterServerManager(){return SpringUtil.getBean(RegisterServerManager.class);}
    public static SipTransactionManager getSipTransactionManager(){return SpringUtil.getBean(SipTransactionManager.class);}
    public static PlatformRegisterManager getPlatformRegisterManager(){return SpringUtil.getBean(PlatformRegisterManager.class);}
    public static CseqManager getCseqManager(){return SpringUtil.getBean(CseqManager.class);}
    public static SendRtpManager getSendRtpManager(){return SpringUtil.getBean(SendRtpManager.class);}
    public static MediaServerManager getMediaServerManager(){return SpringUtil.getBean(MediaServerManager.class);}
    public static StreamChangedManager getStreamChangedManager(){return SpringUtil.getBean(StreamChangedManager.class);}
    public static RecordMp4Manager getRecordMp4Manager(){return SpringUtil.getBean(RecordMp4Manager.class);}
    public static InviteStreamManager getInviteStreamManager(){return SpringUtil.getBean(InviteStreamManager.class);}
    public static SsrcTransactionManager getSsrcTransactionManager(){return SpringUtil.getBean(SsrcTransactionManager.class);}
    public static AgentNotifySubscribeManager getAgentNotifySubscribeManager(){return SpringUtil.getBean(AgentNotifySubscribeManager.class);}

}
