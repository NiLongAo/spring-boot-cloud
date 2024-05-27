package cn.com.tzy.springbootstarterfreeswitch.client.fs.assign;

import cn.com.tzy.springbootstarterfreeswitch.model.fs.AgentVoInfo;
import cn.com.tzy.springbootstarterfreeswitch.client.fs.AgentStrategy;
import org.apache.commons.lang3.RandomUtils;

/**
 * Created by caoliang on 2021/8/3
 * <p>
 * 随机分配
 */
public class RandomAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentVoInfo agentInfo) {
        return RandomUtils.nextLong();
    }
}
