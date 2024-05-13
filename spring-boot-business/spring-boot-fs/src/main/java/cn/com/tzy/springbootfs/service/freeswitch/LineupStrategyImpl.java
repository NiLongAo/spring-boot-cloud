package cn.com.tzy.springbootfs.service.freeswitch;

import cn.com.tzy.springbootstarterfreeswitch.model.call.CallInfo;
import cn.com.tzy.springbootstarterfreeswitch.service.freeswitch.LineupStrategy;
import org.springframework.stereotype.Service;

@Service
public class LineupStrategyImpl implements LineupStrategy {
    @Override
    public Long calculateLevel(CallInfo callInfo) {
        return null;
    }
}
