package cn.com.tzy.springbootstarterquartz.config.factory;

import cn.com.tzy.springbootstarterquartz.config.mode.TimingModel;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Xiaohan.Yuan
 * @version 1.0.0
 * @ClassName TriggerManager.java
 * @Description 触发器管理器, 用来生成触发器
 * @createTime 2021年12月16日
 */

public class TriggerManager {
    private final List<ITriggerFactory> triggerFactories;

    public TriggerManager(List<ITriggerFactory> triggerFactories) {
        this.triggerFactories = triggerFactories;
    }

    /**
     * 生成对应的触发器
     *
     * @param timingModel 触发器model
     * @return org.quartz.Trigger
     * @author YuanXiaohan
     * @date 2021/12/16 2:53 下午
     */
    public Trigger build(TimingModel timingModel) {
        for (ITriggerFactory triggerFactory : triggerFactories) {
            if (triggerFactory.check(timingModel.getType())) {
                return triggerFactory.build(timingModel);
            }
        }
        return null;
    }
}
