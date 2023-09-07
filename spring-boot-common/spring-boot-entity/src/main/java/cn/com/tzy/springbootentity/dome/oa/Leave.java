package cn.com.tzy.springbootentity.dome.oa;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import cn.com.tzy.springbootcomm.constant.Constant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(value = "请假表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "oa_leave")
public class Leave extends LongIdEntity {
    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = Constant.DATE_TIME_FORMAT)
    @JsonFormat(pattern =  Constant.DATE_TIME_FORMAT)
    private Date endTime;

    /**
     * 请假天数
     */
    @TableField(value = "day")
    @ApiModelProperty(value = "请假天数")
    private Integer day;

    /**
     * 流程实例主键
     */
    @TableField(value = "process_instance_id")
    @ApiModelProperty(value = "流程实例主键")
    private String processInstanceId;

    /**
     * 状态 1.审核中 2.审核成功 3.审核不通过
     */
    @TableField(value = "state")
    @ApiModelProperty(value = "状态 1.审核中 2.审核成功 3.审核不通过")
    private Integer state;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;

    /**
     * 发起人ID
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "发起人ID")
    private Long userId;

    /**
     * 发起人名称
     */
    @TableField(value = "user_name")
    @ApiModelProperty(value = "发起人名称")
    private String userName;

    /**
     * 部门ID
     */
    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;

    /**
     * 部门名称
     */
    @TableField(value = "department_name")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}