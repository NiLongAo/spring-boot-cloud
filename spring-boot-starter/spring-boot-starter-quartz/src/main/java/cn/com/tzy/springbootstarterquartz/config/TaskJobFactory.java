package cn.com.tzy.springbootstarterquartz.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;


@Component
public class TaskJobFactory extends AdaptableJobFactory {
    private final AutowireCapableBeanFactory capableBeanFactory;

    public TaskJobFactory(AutowireCapableBeanFactory capableBeanFactory) {
        this.capableBeanFactory = capableBeanFactory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        //进行注入
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
