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
    * 坐席技能表
    */
@ApiModel(description="坐席技能表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_skill_agent")
public class SkillAgent extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 技能id
     */
    @TableField(value = "skill_id")
    @ApiModelProperty(value="技能id")
    private Long skillId;

    /**
     * 坐席id
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席id")
    private Long agentId;

    /**
     * 范围
     */
    @TableField(value = "rank_value")
    @ApiModelProperty(value="范围")
    private Integer rankValue;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}