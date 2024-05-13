package cn.com.tzy.springbootentity.dome.fs;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
    * 溢出策略前置条件
    */
@ApiModel(description="溢出策略前置条件")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_overflow_front")
public class OverflowFront extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 策略ID
     */
    @TableField(value = "overflow_id")
    @ApiModelProperty(value="策略ID")
    private Long overflowId;

    /**
     * 1:队列长度; 2:队列等待最大时长; 3:呼损率
     */
    @TableField(value = "front_type")
    @ApiModelProperty(value="1:队列长度; 2:队列等待最大时长; 3:呼损率")
    private Integer frontType;

    /**
     * 0:全部; 1:小于或等于; 2:等于; 3:大于或等于; 4:大于
     */
    @TableField(value = "compare_condition")
    @ApiModelProperty(value="0:全部; 1:小于或等于; 2:等于; 3:大于或等于; 4:大于")
    private Integer compareCondition;

    @TableField(value = "rank_value_start")
    @ApiModelProperty(value="")
    private Integer rankValueStart;

    /**
     * 符号条件值
     */
    @TableField(value = "rank_value")
    @ApiModelProperty(value="符号条件值")
    private Integer rankValue;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}