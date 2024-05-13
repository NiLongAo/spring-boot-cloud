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
    * 溢出策略表
    */
@ApiModel(description="溢出策略表")
@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "fs_overflow_config")
public class OverflowConfig extends LongIdEntity {
    /**
     * 企业id
     */
    @TableField(value = "company_id")
    @ApiModelProperty(value="企业id")
    private Long companyId;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="名称")
    private String name;

    /**
     * 1:排队,2:溢出,3:挂机
     */
    @TableField(value = "handle_type")
    @ApiModelProperty(value="1:排队,2:溢出,3:挂机")
    private Integer handleType;

    /**
     * 排队方式(1:先进先出,2:vip,3:自定义)
     */
    @TableField(value = "busy_type")
    @ApiModelProperty(value="排队方式(1:先进先出,2:vip,3:自定义)")
    private Integer busyType;

    /**
     * 排队超时时间
     */
    @TableField(value = "queue_timeout")
    @ApiModelProperty(value="排队超时时间")
    private Integer queueTimeout;

    /**
     * 排队超时(1:溢出,2:挂机)
     */
    @TableField(value = "busy_timeout_type")
    @ApiModelProperty(value="排队超时(1:溢出,2:挂机)")
    private Integer busyTimeoutType;

    /**
     * 溢出(1:group,2:ivr,3:vdn)
     */
    @TableField(value = "overflow_type")
    @ApiModelProperty(value="溢出(1:group,2:ivr,3:vdn)")
    private Integer overflowType;

    /**
     * 溢出值
     */
    @TableField(value = "overflow_value")
    @ApiModelProperty(value="溢出值")
    private Integer overflowValue;

    /**
     * 自定义排队表达式
     */
    @TableField(value = "lineup_expression")
    @ApiModelProperty(value="自定义排队表达式")
    private String lineupExpression;
}