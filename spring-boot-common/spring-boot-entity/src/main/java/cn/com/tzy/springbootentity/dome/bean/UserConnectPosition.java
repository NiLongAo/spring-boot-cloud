package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.Base;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "用户关联职位表")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_user_connect_position")
public class UserConnectPosition extends Base {
    /**
     * 用户编号
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户编号")
    private Long userId;

    /**
     * 部门编号
     */
    @TableField(value = "position_id")
    @ApiModelProperty(value = "部门编号")
    private Long positionId;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户编号")
    private Long tenantId;
}
