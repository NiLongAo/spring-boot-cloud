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
    * 技能组中坐席分配策略
    */
@ApiModel(description="技能组中坐席分配策略")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group_agent_strategy")
public class GroupAgentStrategy extends LongIdEntity {
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
     * 1:内置策略,2:自定义
     */
    @TableField(value = "strategy_type")
    @ApiModelProperty(value="1:内置策略,2:自定义")
    private Integer strategyType;

    /**
     * (1最长空闲时间、2最长平均空闲、3最少应答次数、4最少通话时长、5最长话后时长、6轮选、7随机)
     */
    @TableField(value = "strategy_value")
    @ApiModelProperty(value="(1最长空闲时间、2最长平均空闲、3最少应答次数、4最少通话时长、5最长话后时长、6轮选、7随机)")
    private Integer strategyValue;

    /**
     * 自定义表达式
     */
    @TableField(value = "custom_expression")
    @ApiModelProperty(value="自定义表达式")
    private String customExpression;

    @TableField(value = "`status`")
    @ApiModelProperty(value="")
    private Integer status;
}