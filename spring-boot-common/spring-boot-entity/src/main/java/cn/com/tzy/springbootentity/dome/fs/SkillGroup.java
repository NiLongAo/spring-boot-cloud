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
    * 技能组技能表
    */
@ApiModel(description="技能组技能表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_skill_group")
public class SkillGroup extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    @TableField(value = "level_value")
    @ApiModelProperty(value="")
    private Integer levelValue;

    /**
     * 技能ID
     */
    @TableField(value = "skill_id")
    @ApiModelProperty(value="技能ID")
    private Long skillId;

    /**
     * 技能组ID
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组ID")
    private Long groupId;

    /**
     * 等级类型(1:全部,2:等于,3:>,4:<,5:介于)
     */
    @TableField(value = "rank_type")
    @ApiModelProperty(value="等级类型(1:全部,2:等于,3:>,4:<,5:介于)")
    private Integer rankType;

    /**
     * 介于的开始值
     */
    @TableField(value = "rank_value_start")
    @ApiModelProperty(value="介于的开始值")
    private Integer rankValueStart;

    /**
     * 等级值
     */
    @TableField(value = "rank_value")
    @ApiModelProperty(value="等级值")
    private Integer rankValue;

    /**
     * 匹配规则(1:低到高,2:高到低)
     */
    @TableField(value = "match_type")
    @ApiModelProperty(value="匹配规则(1:低到高,2:高到低)")
    private Integer matchType;

    /**
     * 占用率
     */
    @TableField(value = "share_value")
    @ApiModelProperty(value="占用率")
    private Integer shareValue;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}