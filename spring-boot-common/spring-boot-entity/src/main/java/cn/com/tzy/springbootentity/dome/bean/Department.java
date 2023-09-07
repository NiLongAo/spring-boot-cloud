package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.LongIdEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ApiModel(value = "部门表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_department")
public class Department extends LongIdEntity {

    /**
     * 父级菜单
     */
    @TableField(value = "parent_id",insertStrategy = FieldStrategy.IGNORED,updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "父级菜单")
    private Long parentId;

    /**
     * 部门名称
     */
    @TableField(value = "department_name")
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    /**
     * 是否开启 1.是 0否
     */
    @TableField(value = "is_enable")
    @ApiModelProperty(value = "是否开启 1.是 0否")
    private Integer isEnable;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}
