package cn.com.tzy.springbootstarterquartz.config.mode;

import cn.com.tzy.springbootcomm.common.enumcom.ConstEnum;
import cn.com.tzy.springbootstarterquartz.config.task.QuartzTaskJob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author Xiaohan.Yuan
 * @version 1.0.0
 * @ClassName CronTimingModel.java
 * @Description cron触发器model
 * @createTime 2021年12月16日
 */
@Getter
@Setter
@NoArgsConstructor
public class CronTimingModel extends TimingModel{

    public CronTimingModel(Class<? extends QuartzTaskJob> taskClass, String taskName, String groupName, String description, Map<String, Object> param, String cronExpression) {
        super(taskClass, taskName, groupName, description, ConstEnum.TriggerType.CRON.getValue(), param);
        this.cronExpression = cronExpression;
    }

    public CronTimingModel(Class<? extends QuartzTaskJob> taskClass, String taskName, String groupName, String description,String cronExpression) {
        super(taskClass, taskName, groupName, description, ConstEnum.TriggerType.CRON.getValue());
        this.cronExpression = cronExpression;
    }
}
