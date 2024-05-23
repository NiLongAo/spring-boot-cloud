package cn.com.tzy.springbootstarterfreeswitch.pool.assign;


import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.pool.AgentStrategy;

/**
 * Created by caoliang on 2021/8/3
 * <p>
 * 累计话后时间长优先
 */
public class TotalAfterTimeAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentVoInfo agentInfo) {
        return agentInfo.getTotalAfterTime();
    }
}
