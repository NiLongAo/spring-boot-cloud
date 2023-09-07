package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "用户基本表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_user_set")
public class UserSet extends LongIdEntity {
    /**
     * 是否核心管理员 1是 0否
     */
    @TableField(value = "is_admin")
    @ApiModelProperty(value = "是否核心管理员 1是 0否")
    private Integer isAdmin;

    /**
     * 是否禁止登录 1是 0否
     */
    @TableField(value = "is_enabled")
    @ApiModelProperty(value = "是否禁止登录 1是 0否")
    private Integer isEnabled;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}