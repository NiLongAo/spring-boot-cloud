package cn.com.tzy.springbootstarterfreeswitch.pool.assign;


import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.pool.AgentStrategy;

/**
 * Created by caoliang on 2021/8/3
 * <p>
 * 最少通话时长优先分配
 */
public class LeastTalkAssign implements AgentStrategy {
    @Override
    public Long calculateLevel(AgentVoInfo agentInfo) {
        return -agentInfo.getTotalTalkTime();
    }
}
