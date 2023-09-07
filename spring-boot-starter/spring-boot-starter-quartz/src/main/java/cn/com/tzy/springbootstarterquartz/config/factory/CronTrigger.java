package cn.com.tzy.springbootstarterquartz.config.factory;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.excption.TimingException;
import cn.com.tzy.springbootstarterquartz.config.mode.CronTimingModel;
import cn.com.tzy.springbootstarterquartz.config.mode.TimingModel;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;


public class CronTrigger implements ITriggerFactory {

    @Override
    public boolean check(Integer triggerType) {
        return triggerType== ConstEnum.TriggerType.CRON.getValue();
    }

    @Override
    public Trigger build(TimingModel timingModel) {
        if (!(timingModel instanceof CronTimingModel)){
            throw new TimingException("构建类型为CRON定时必须传入CronTimingModel.class的实现类");
        }
        //按新的cronExpression表达式构建一个新的trigger
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(((CronTimingModel) timingModel).getCronExpression());

        TriggerBuilder<org.quartz.CronTrigger> cronTriggerTriggerBuilder = TriggerBuilder.newTrigger().withIdentity(timingModel.getTaskName(), timingModel.getTaskName())
                .withSchedule(scheduleBuilder);

        if (timingModel.getStartTime()!=null){
            cronTriggerTriggerBuilder.startAt(timingModel.getStartTime());
        }
        if (timingModel.getEndTime()!=null){
            cronTriggerTriggerBuilder.endAt(timingModel.getEndTime());
        }
        return cronTriggerTriggerBuilder.build();
    }
}
