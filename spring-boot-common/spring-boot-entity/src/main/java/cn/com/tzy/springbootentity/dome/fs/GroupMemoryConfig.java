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
    * 技能组坐席记忆配置表
    */
@ApiModel(description="技能组坐席记忆配置表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group_memory_config")
public class GroupMemoryConfig extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 技能组ID
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组ID")
    private Long groupId;

    /**
     * 匹配成功策略
     */
    @TableField(value = "success_strategy")
    @ApiModelProperty(value="匹配成功策略")
    private Integer successStrategy;

    /**
     * 匹配成功策略值
     */
    @TableField(value = "success_strategy_value")
    @ApiModelProperty(value="匹配成功策略值")
    private Long successStrategyValue;

    /**
     * 匹配失败策略
     */
    @TableField(value = "fail_strategy")
    @ApiModelProperty(value="匹配失败策略")
    private Integer failStrategy;

    /**
     * 匹配失败策略值
     */
    @TableField(value = "fail_strategy_value")
    @ApiModelProperty(value="匹配失败策略值")
    private Long failStrategyValue;

    /**
     * 记忆天数
     */
    @TableField(value = "memory_day")
    @ApiModelProperty(value="记忆天数")
    private Integer memoryDay;

    /**
     * 呼入覆盖
     */
    @TableField(value = "inbound_cover")
    @ApiModelProperty(value="呼入覆盖")
    private Integer inboundCover;

    /**
     * 外呼覆盖
     */
    @TableField(value = "outbound_cover")
    @ApiModelProperty(value="外呼覆盖")
    private Integer outboundCover;

    @TableField(value = "`status`")
    @ApiModelProperty(value="")
    private Integer status;
}