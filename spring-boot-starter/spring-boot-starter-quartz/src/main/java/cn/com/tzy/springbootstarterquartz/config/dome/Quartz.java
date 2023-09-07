package cn.com.tzy.springbootstarterquartz.config.dome;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Quartz extends LongIdEntity {
    /**
     * 包路径
     */
    protected String classesName;

    /**
     * cron表达式
     */
    protected String cronExpression;

    /**
     * 任务名
     */
    protected String taskName;

    /**
     * 任务组名
     */
    protected String groupName;

    /**
     * 任务描述
     */
    protected String description;

    /**
     * 任务类型
     */
    protected Integer type;

    /**
     * 任务状态
     */
    protected Integer taskStatus;

    /**
     * 开始时间
     */
    protected Date startTime;

    /**
     * 结束时间
     */
    protected Date endTime;
}