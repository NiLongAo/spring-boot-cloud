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
    * 坐席与客户记忆表
    */
@ApiModel(description="坐席与客户记忆表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group_memory")
public class GroupMemory extends LongIdEntity {
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
     * 坐席
     */
    @TableField(value = "agent_key")
    @ApiModelProperty(value="坐席")
    private String agentKey;

    /**
     * 客户电话
     */
    @TableField(value = "phone")
    @ApiModelProperty(value="客户电话")
    private String phone;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}