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
    * 坐席自定义策略表
    */
@ApiModel(description="坐席自定义策略表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group_strategy_exp")
public class GroupStrategyExp extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 技能组id
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组id")
    private Long groupId;

    /**
     * 自定义值
     */
    @TableField(value = "strategy_key")
    @ApiModelProperty(value="自定义值")
    private String strategyKey;

    /**
     * 百分百
     */
    @TableField(value = "strategy_present")
    @ApiModelProperty(value="百分百")
    private Integer strategyPresent;

    /**
     * 类型
     */
    @TableField(value = "strategy_type")
    @ApiModelProperty(value="类型")
    private Integer strategyType;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}