package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "用户关联部门表")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_user_connect_department")
public class UserConnectDepartment extends Base {
    /**
     * 用户编号
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户编号")
    private Long userId;

    /**
     * 部门编号
     */
    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门编号")
    private Long departmentId;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}
