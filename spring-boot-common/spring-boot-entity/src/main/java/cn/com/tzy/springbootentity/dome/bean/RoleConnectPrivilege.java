package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "角色关联权限表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_role_connect_privilege")
public class RoleConnectPrivilege extends Base {
    /**
     * 角色编号
     */
    @TableField(value = "role_id")
    @ApiModelProperty(value = "角色编号")
    private Long roleId;

    /**
     * 权限编号
     */
    @TableField(value = "privilege_id")
    @ApiModelProperty(value = "权限编号")
    private String privilegeId;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}
