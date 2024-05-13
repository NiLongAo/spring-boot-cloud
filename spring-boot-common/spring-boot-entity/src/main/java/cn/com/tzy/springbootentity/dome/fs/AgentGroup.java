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
    * 坐席技能组表
    */
@ApiModel(description="坐席技能组表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_agent_group")
public class AgentGroup extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 坐席id
     */
    @TableField(value = "agent_id")
    @ApiModelProperty(value="坐席id")
    private Long agentId;

    @TableField(value = "agent_key")
    @ApiModelProperty(value="")
    private String agentKey;

    @TableField(value = "agent_type")
    @ApiModelProperty(value="")
    private Integer agentType;

    /**
     * 技能组id
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组id")
    private Long groupId;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}