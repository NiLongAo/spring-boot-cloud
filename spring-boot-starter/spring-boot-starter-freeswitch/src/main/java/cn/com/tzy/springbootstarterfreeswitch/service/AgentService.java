package cn.com.tzy.springbootstarterfreeswitch.service;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentInfo;

public interface AgentService {
    public AgentInfo getAgentBySip(String sip);
}
