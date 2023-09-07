package cn.com.tzy.springbootentity.dome.sms;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@ApiModel(value="定时器任务表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_quartz")
public class Quartz extends LongIdEntity {
    /**
     * 包路径
     */
    @TableField(value = "classes_name")
    @ApiModelProperty(value="包路径")
    protected String classesName;

    /**
     * cron表达式
     */
    @TableField(value = "cron_expression")
    @ApiModelProperty(value="cron表达式")
    protected String cronExpression;

    /**
     * 任务名
     */
    @TableField(value = "task_name")
    @ApiModelProperty(value="任务名")
    protected String taskName;

    /**
     * 任务组名
     */
    @TableField(value = "group_name")
    @ApiModelProperty(value="任务组名")
    protected String groupName;

    /**
     * 任务描述
     */
    @TableField(value = "description")
    @ApiModelProperty(value="任务描述")
    protected String description;

    /**
     * 任务类型
     */
    @TableField(value = "type")
    @ApiModelProperty(value="任务类型")
    protected Integer type;

    /**
     * 任务状态
     */
    @TableField(value = "task_status")
    @ApiModelProperty(value="任务状态")
    protected Integer taskStatus;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(value="开始时间")
    protected Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="结束时间")
    protected Date endTime;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}