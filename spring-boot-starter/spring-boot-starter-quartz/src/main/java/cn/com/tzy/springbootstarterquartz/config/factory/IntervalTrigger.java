package cn.com.tzy.springbootstarterquartz.config.factory;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootcomm.excption.TimingException;
import cn.com.tzy.springbootstarterquartz.config.mode.IntervalTimingMode;
import cn.com.tzy.springbootstarterquartz.config.mode.TimingModel;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;


public class IntervalTrigger implements ITriggerFactory {
    @Override
    public boolean check(Integer triggerType) {
        return triggerType == ConstEnum.TriggerType.INTERVAL_MINUTE.getValue() || triggerType == ConstEnum.TriggerType.INTERVAL_SECOND.getValue() || triggerType == ConstEnum.TriggerType.INTERVAL_MILLISECOND.getValue()||triggerType == ConstEnum.TriggerType.INTERVAL_HOUR.getValue();
    }

    @Override
    public Trigger build(TimingModel timingModel) {
        if (!(timingModel instanceof IntervalTimingMode)){
            throw new TimingException("构建类型为INTERVAL定时必须传入IntervalTimingMode.class的实现类");
        }
        //创建触发器
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        Long interval = ((IntervalTimingMode) timingModel).getInterval();
        Integer repeatCount = ((IntervalTimingMode) timingModel).getRepeatCount();
        switch (ConstEnum.TriggerType.getTriggerType(timingModel.getType())){
            case INTERVAL_MINUTE:
                simpleScheduleBuilder.withIntervalInMinutes(Math.toIntExact(interval));
                break;
            case INTERVAL_HOUR:
                simpleScheduleBuilder.withIntervalInHours(Math.toIntExact(interval));
                break;
            case INTERVAL_SECOND:
                simpleScheduleBuilder.withIntervalInSeconds(Math.toIntExact(interval));
                break;
            case INTERVAL_MILLISECOND:
                simpleScheduleBuilder.withIntervalInMilliseconds(interval);
                break;
        }
        // 重复次数，为空则无限重复
        if (repeatCount==null){
            simpleScheduleBuilder.repeatForever();
        }else {
            simpleScheduleBuilder.withRepeatCount(repeatCount);
        }

        TriggerBuilder<SimpleTrigger> simpleTriggerTriggerBuilder = TriggerBuilder.newTrigger().withIdentity(timingModel.getTaskName(), timingModel.getTaskName())
                .withSchedule(simpleScheduleBuilder);
        // 开始结束时间
        if (timingModel.getStartTime()!=null){
            simpleTriggerTriggerBuilder.startAt(timingModel.getStartTime());
        }
        if (timingModel.getEndTime()!=null){
            simpleTriggerTriggerBuilder.endAt(timingModel.getEndTime());
        }
        return simpleTriggerTriggerBuilder.build();
    }
}
