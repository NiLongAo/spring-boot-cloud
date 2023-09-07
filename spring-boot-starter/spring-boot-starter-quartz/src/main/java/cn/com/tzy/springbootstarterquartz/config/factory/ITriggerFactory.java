package cn.com.tzy.springbootstarterquartz.config.factory;

import cn.com.tzy.springbootstarterquartz.config.mode.TimingModel;
import org.quartz.Trigger;

/**
 * @author Xiaohan.Yuan
 * @version 1.0.0
 * @ClassName TriggerHandler.java
 * @Description 触发器工厂
 * @createTime 2021年12月16日
 */
public interface ITriggerFactory {

    /**
     * 判断是否为该类型的触发器
     *
     * @param triggerType 触发器类型
     * @return boolean 如果是该类型的触发器返回true 否则返回false
     * @author YuanXiaohan
     * @date 2021/12/16 2:33 下午
     */
    public boolean check(Integer triggerType);


    public Trigger build(TimingModel timingModel);
}
