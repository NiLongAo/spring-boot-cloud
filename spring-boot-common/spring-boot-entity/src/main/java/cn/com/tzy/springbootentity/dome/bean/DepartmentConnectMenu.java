package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "部门关联权限表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_department_connect_menu")
public class DepartmentConnectMenu extends Base {
    /**
     * 部门编号
     */
    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门编号")
    private Long departmentId;

    /**
     * 菜单编号
     */
    @TableField(value = "menu_id")
    @ApiModelProperty(value = "菜单编号")
    private String menuId;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}
