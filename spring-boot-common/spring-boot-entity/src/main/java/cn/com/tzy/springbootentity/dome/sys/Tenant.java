package cn.com.tzy.springbootentity.dome.sys;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value="租户基本信息")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_tenant")
public class Tenant extends LongIdEntity {
    /**
     * 租户名
     */
    @TableField(value = "tenant_name")
    @ApiModelProperty(value="租户名")
    private String tenantName;

    /**
     * 租户联系人编号
     */
    @TableField(value = "tenant_user_id")
    @ApiModelProperty(value="租户联系人编号")
    private Long tenantUserId;

    /**
     * 租户联系人名称
     */
    @TableField(value = "tenant_user_name")
    @ApiModelProperty(value="租户联系人名称")
    private String tenantUserName;

    /**
     * 租户状态（0正常 1停用）
     */
    @TableField(value = "status")
    @ApiModelProperty(value="租户状态（0停用 1正常）")
    private Integer status;

    /**
     * 账号数量
     */
    @TableField(value = "account_count")
    @ApiModelProperty(value="账号数量")
    private Integer accountCount;
}