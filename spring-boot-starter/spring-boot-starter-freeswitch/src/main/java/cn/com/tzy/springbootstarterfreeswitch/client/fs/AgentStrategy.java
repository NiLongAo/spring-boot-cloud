package cn.com.tzy.springbootstarterfreeswitch.client.fs;


import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;

/**
 * Created by caoliang on 2021/8/2
 * <p>
 * 坐席在技能组空闲策略
 */
public interface AgentStrategy {

    /**
     * 坐席空闲策略
     *
     * @param agentVoInfo
     * @return
     */
    Long calculateLevel(AgentVoInfo agentVoInfo);
}
