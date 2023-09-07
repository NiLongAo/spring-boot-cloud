package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
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
@TableName(value = "bean_department_connect_privilege")
public class DepartmentConnectPrivilege extends Base {
    /**
     * 部门编号
     */
    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门编号")
    private Long departmentId;

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
