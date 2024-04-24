package cn.com.tzy.springbootstarterfreeswitch.redis;

import cn.com.tzy.springbootstarterfreeswitch.redis.impl.*;
import cn.hutool.extra.spring.SpringUtil;

public class RedisService {

    public static CallInfoManager getCallInfoManager(){return SpringUtil.getBean(CallInfoManager.class);}
    public static VdnPhoneManager getVdnPhoneManager(){return SpringUtil.getBean(VdnPhoneManager.class);}
    public static CompanyInfoManager getCompanyInfoManager(){return SpringUtil.getBean(CompanyInfoManager.class);}
    public static GroupInfoManager getGroupInfoManager(){return SpringUtil.getBean(GroupInfoManager.class);}
    public static AgentInfoManager getAgentInfoManager(){return SpringUtil.getBean(AgentInfoManager.class);}
    public static DeviceInfoManager getDeviceInfoManager(){return SpringUtil.getBean(DeviceInfoManager.class);}
    public static PlaybackInfoManager getPlaybackInfoManager(){return SpringUtil.getBean(PlaybackInfoManager.class);}


}
