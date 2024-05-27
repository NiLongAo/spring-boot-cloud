package cn.com.tzy.springbootstarterfreeswitch.client.fs.assign;


import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.AgentStrategy;

/**
 * Created by caoliang on 2021/8/3
 * <p>
 * 最少应答次数优先
 */
public class LeastAnswerAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentVoInfo agentInfo) {
        return -agentInfo.getTotalAnswerTimes();
    }
}
