package cn.com.tzy.springbootstarterfreeswitch.pool.assign;


import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.pool.AgentStrategy;

/**
 * Created by caoliang on 2021/8/3
 * <p>
 * 空闲次数最多优先
 */
public class TotalReadyTimesAssign implements AgentStrategy {
    @Override
    public Long calculateLevel(AgentVoInfo agentInfo) {
        return agentInfo.getReadyTimes();
    }
}
