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
    * 技能组排队策略表
    */
@ApiModel(description="技能组排队策略表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_group_overflow")
public class GroupOverflow extends LongIdEntity {
    /**
     * 技能组ID
     */
    @TableField(value = "group_id")
    @ApiModelProperty(value="技能组ID")
    private Long groupId;

    /**
     * 溢出策略ID
     */
    @TableField(value = "overflow_id")
    @ApiModelProperty(value="溢出策略ID")
    private Long overflowId;

    /**
     * 优先级
     */
    @TableField(value = "level_value")
    @ApiModelProperty(value="优先级")
    private Integer levelValue;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}