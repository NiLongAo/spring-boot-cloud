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
    * 自定义溢出策略优先级
    */
@ApiModel(description="自定义溢出策略优先级")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_overflow_exp")
public class OverflowExp extends LongIdEntity {
    /**
     * 企业ID
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业ID")
    private Long companyId;

    /**
     * 溢出策略ID
     */
    @TableField(value = "overflow_id")
    @ApiModelProperty(value="溢出策略ID")
    private Long overflowId;

    /**
     * 自定义值
     */
    @TableField(value = "exp_key")
    @ApiModelProperty(value="自定义值")
    private String expKey;

    /**
     * 权重
     */
    @TableField(value = "rate")
    @ApiModelProperty(value="权重")
    private Integer rate;

    /**
     * 状态
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;
}