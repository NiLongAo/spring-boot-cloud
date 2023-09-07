package cn.com.tzy.springbootentity.dome.bean;

import cn.com.tzy.springbootcomm.common.bean.StringIdEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@ApiModel(value = "权限表")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bean_privilege")
public class Privilege extends StringIdEntity {
    /**
     * 权限名称
     */
    @TableField(value = "privilege_name")
    @ApiModelProperty(value = "权限名称")
    private String privilegeName;

    /**
     * 是否开启 1.是 0否
     */
    @TableField(value = "is_open")
    @ApiModelProperty(value = "是否开启 1.是 0否")
    private Integer isOpen;

    /**
     * 请求路径
     */
    @TableField(value = "request_url")
    @ApiModelProperty(value = "请求路径")
    private String requestUrl;

    /**
     * 菜单编号
     */
    @TableField(value = "menu_id")
    @ApiModelProperty(value = "菜单编号")
    private String menuId;

    /**
     * 备注
     */
    @TableField(value = "memo")
    @ApiModelProperty(value = "备注")
    private String memo;
}